package com.smotana.clearflask.web.security;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.net.InternetDomainName;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.kik.config.ice.ConfigSystem;
import com.kik.config.ice.annotations.DefaultValue;
import com.smotana.clearflask.util.LogUtil;
import com.smotana.clearflask.web.ErrorWithMessageException;
import lombok.extern.slf4j.Slf4j;
import org.owasp.html.Handler;
import org.owasp.html.HtmlChangeListener;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.HtmlSanitizer;
import org.owasp.html.HtmlStreamRenderer;
import org.owasp.html.PolicyFactory;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Type;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Slf4j
@Singleton
public class Sanitizer {
    private static final PolicyFactory HtmlToPlaintextPolicyFactory = new HtmlPolicyBuilder().toFactory();

    /** If changed, also change in Sanitizer.java */
    private static final PolicyFactory RichHtmlPolicyFactory = new HtmlPolicyBuilder()
            .allowAttributes("class").matching(Pattern.compile("ql-indent-[0-9]")).onElements("li")
            .allowAttributes("class").matching(false, "ql-syntax").onElements("pre")
            .allowAttributes("spellcheck").matching(false, "false").onElements("pre")
            .allowAttributes("data-checked").matching(false, "true", "false").onElements("ul")

            // Links
            .allowAttributes("href").onElements("a")
            // If changed, also change in quill-format-link.ts
            .allowAttributes("target").matching((String elementName, String attributeName, String value) -> "_blank").onElements("a")
            .allowAttributes("rel").matching((String elementName, String attributeName, String value) -> "" /* Will be set by requireRelsOnLinks */).onElements("a")
            .allowElements((elementName, attrs) -> attrs.containsAll(ImmutableSet.of("rel", "href", "target")) ? elementName : null, "a")
            // If changed, also change in quill-format-link.ts
            .requireRelsOnLinks("noreferrer", "noopener", "ugc")
            // If changed, also change in quill-format-link.ts
            .allowUrlProtocols("https", "http", "mailto", "tel")

            // Migration from <p> to <div>
            .allowElements((elementName, attrs) -> "div", "p")

            .allowElements("div", "br", "a", "strong", "s", "em", "u", "ul", "ol", "li", "pre", "blockquote")
            .toFactory();

    public interface Config {
        @DefaultValue(value = "www,admin,smotana,clearflask,veruv,mail,email,remote,blog,server,ns1,ns2,smtp,secure,vpn,m,shop,portal,support,dev,news,kaui,killbill,kibana,feedback,docs,documentation,release,api,domain,cname,sni", innerType = String.class)
        Set<String> reservedSubdomains();

        @DefaultValue(value = "clearflask.com", innerType = String.class)
        Set<String> reservedDomains();

        @DefaultValue("sni.clearflask.com")
        String sniDomain();

        @DefaultValue("true")
        boolean htmlSanitizerEnabled();

        @DefaultValue("<p style=\"color: #e60000;\">Cannot display corrupted message</p>")
        boolean htmlSanitizerInvalidHtmlMessage();
    }

    @Inject
    private Config config;

    /** If changed, also change in IdeaExplorer.tsx */
    private static final long POST_TITLE_MAX_LENGTH = 100;
    private static final long CONTENT_MAX_LENGTH = 10_000;
    private static final long NAME_MAX_LENGTH = 30;
    /** If changed, also change in api-project.yaml */
    private static final long SUBDOMAIN_MIN_LENGTH = 1;
    /** If changed, also change in api-project.yaml */
    private static final long SUBDOMAIN_MAX_LENGTH = 30;
    /** If changed, also change in api-project.yaml */
    private static final String SUBDOMAIN_REGEX = "^[a-z0-9](?:[a-z0-9\\-]*[a-z0-9])?$";
    private static final long SEARCH_TEXT_MAX_LENGTH = 200;

    private final Predicate<String> subdomainPredicate;

    @Inject
    private Sanitizer() {
        subdomainPredicate = Pattern.compile(SUBDOMAIN_REGEX).asPredicate();
    }

    public void email(String email) {
        if (email == null) {
            return;
        }
        try {
            new InternetAddress(email).validate();
        } catch (AddressException ex) {
            throw new ErrorWithMessageException(BAD_REQUEST, "Invalid email format", ex);
        }
    }

    public void accountName(String accountName) {
        if (accountName != null && accountName.length() > NAME_MAX_LENGTH) {
            throw new ErrorWithMessageException(BAD_REQUEST, "Name is too long, must be at most " + NAME_MAX_LENGTH + " characters");
        }
    }

    public void userName(String userName) {
        if (userName != null && userName.length() > NAME_MAX_LENGTH) {
            throw new ErrorWithMessageException(BAD_REQUEST, "Name is too long, must be at most " + NAME_MAX_LENGTH + " characters");
        }
    }

    public void content(String content) {
        if (content != null && content.length() > CONTENT_MAX_LENGTH) {
            throw new ErrorWithMessageException(BAD_REQUEST, "Content is too long, must be at most " + CONTENT_MAX_LENGTH + " characters");
        }
    }

    public void postTitle(String postTitle) {
        if (postTitle != null && postTitle.length() > POST_TITLE_MAX_LENGTH) {
            throw new ErrorWithMessageException(BAD_REQUEST, "Title is too long, must be at most " + POST_TITLE_MAX_LENGTH + " characters");
        }
    }

    public void searchText(String searchText) {
        if (searchText != null && searchText.length() > SEARCH_TEXT_MAX_LENGTH) {
            throw new ErrorWithMessageException(BAD_REQUEST, "Search text is too long, must be at most " + SEARCH_TEXT_MAX_LENGTH + " characters");
        }
    }

    public void domain(String domain) {
        if (Strings.isNullOrEmpty(domain)) {
            throw new ErrorWithMessageException(BAD_REQUEST, "Custom domain is empty");
        }

        if (!InternetDomainName.from(domain).isUnderPublicSuffix()) {
            throw new ErrorWithMessageException(BAD_REQUEST, "Custom domain doesn't appear to have a public suffix. If this is an error, please contact support team.");
        }

        try {
            boolean isCanonical = Arrays.stream((CNAMERecord[]) new Lookup(domain, Type.CNAME).getAnswers())
                    .allMatch(r -> r.getType() == Type.CNAME
                            && config.sniDomain().equals(r.getTarget().toString(true)));
            if (!isCanonical) {
                throw new ErrorWithMessageException(BAD_REQUEST, "Custom domain doesn't appear to have the correct DNS entry. Please set a CNAME record in your DNS to " + config.sniDomain());
            }
        } catch (Exception ex) {
            throw new ErrorWithMessageException(BAD_REQUEST, "Custom domain doesn't appear to have the correct DNS entry. Please set a CNAME record in your DNS to " + config.sniDomain());
        }

        if (config.reservedDomains().contains(domain)) {
            throw new ErrorWithMessageException(Response.Status.BAD_REQUEST, "'" + domain + "' domain is reserved");
        }
    }

    public void subdomain(String subdomain) {
        if (subdomain.length() < SUBDOMAIN_MIN_LENGTH) {
            throw new ErrorWithMessageException(BAD_REQUEST, "Subdomain is too short, must be at least " + SUBDOMAIN_MIN_LENGTH + " character(s)");
        }
        if (subdomain.length() > SUBDOMAIN_MAX_LENGTH) {
            throw new ErrorWithMessageException(BAD_REQUEST, "Subdomain is too long, must be at most " + SUBDOMAIN_MAX_LENGTH + " characters");
        }
        if (!subdomainPredicate.test(subdomain)) {
            throw new ErrorWithMessageException(BAD_REQUEST, "Subdomain can only contain lowercase letters, numbers and dashes in the middle");
        }

        if (config.reservedSubdomains().contains(subdomain)) {
            throw new ErrorWithMessageException(Response.Status.BAD_REQUEST, "'" + subdomain + "' subdomain is reserved");
        }
    }

    public String richHtml(String html, String identifierType, String identifierId) {
        if (!config.htmlSanitizerEnabled()) {
            return html;
        }
        StringBuilder sanitizedHtmlBuilder = new StringBuilder();

        Map<String, Set<String>> discarded = Maps.newHashMap();
        HtmlChangeListener<Map<String, Set<String>>> htmlChangeListener = new HtmlChangeListener<>() {
            @Override
            public void discardedTag(Map<String, Set<String>> discarded, String elementName) {
                discarded.putIfAbsent(elementName, Sets.newHashSet());
            }

            @Override
            public void discardedAttributes(Map<String, Set<String>> discarded, String elementName, String... attributeNames) {
                discarded.getOrDefault(elementName, Sets.newHashSet()).addAll(Arrays.asList(attributeNames));
            }
        };
        HtmlStreamRenderer renderer = HtmlStreamRenderer.create(
                sanitizedHtmlBuilder,
                badHtml -> {
                    if (LogUtil.rateLimitAllowLog("sanitizer-html-error")) {
                        log.warn("Error in HTML parsing for {} id {}: '{}'",
                                identifierType, identifierId, badHtml);
                    }
                    sanitizedHtmlBuilder.append(config.htmlSanitizerInvalidHtmlMessage());
                });
        HtmlSanitizer.sanitize(html, RichHtmlPolicyFactory.apply(renderer, htmlChangeListener, discarded));

        // Migration from <p> to <div>
        if (!discarded.isEmpty()
                && discarded.containsKey("p")
                && discarded.get("p").isEmpty()) {
            discarded.remove("p");
        }

        if (!discarded.isEmpty()) {
            if (LogUtil.rateLimitAllowLog("sanitizer-html-violation")) {
                log.warn("HTML Policy violation(s) for {} id {}, element-attribute violations(s): {}",
                        identifierType, identifierId, discarded);
            }
        }
        return sanitizedHtmlBuilder.toString();
    }

    public String richHtmlToPlaintext(String html) {
        StringBuilder sanitizedHtmlBuilder = new StringBuilder();

        HtmlStreamRenderer renderer = HtmlStreamRenderer.create(
                sanitizedHtmlBuilder,
                Handler.DO_NOTHING);
        HtmlSanitizer.sanitize(html, HtmlToPlaintextPolicyFactory.apply(renderer));
        return sanitizedHtmlBuilder.toString();
    }

    public static Module module() {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bind(Sanitizer.class).asEagerSingleton();
                install(ConfigSystem.configModule(Config.class));
            }
        };
    }
}
