package com.smotana.clearflask.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.smotana.clearflask.web.security.AuthCookieUtil;
import com.smotana.clearflask.web.security.AuthCookieUtil.AccountAuthCookie;
import com.smotana.clearflask.web.security.AuthCookieUtil.UserAuthCookie;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.message.internal.HttpDateFormat;
import org.glassfish.jersey.message.internal.StringBuilderUtils;

import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
@Value
public final class RealCookie {

    public enum SameSite {
        NONE,
        STRICT,
        LAX,
    }

    @NonNull
    private final String name;
    @NonNull
    private final String value;
    @NonNull
    private final String path;
    @NonNull
    private final long version;
    private final String domain;

    private final boolean httpOnly;
    private final boolean secure;
    private final SameSite sameSite;

    private final Long maxAge;
    private final Instant expiry;

    private final String comment;

    @NonNull
    private final ImmutableMap<String, String> additionalProperties;

    @Builder
    public RealCookie(
            @NonNull String name,
            @NonNull String value,
            @NonNull String path,
            @NonNull long version,
            String domain,
            boolean httpOnly,
            boolean secure,
            SameSite sameSite,
            Long maxAge,
            Instant expiry,
            String comment,
            @NonNull ImmutableMap<String, String> additionalProperties) {
        checkArgument(secure || sameSite != SameSite.NONE, "Cookies with SameSite=None must also specify Secure");
        this.name = name;
        this.value = value;
        this.path = path;
        this.version = version;
        this.domain = domain;
        this.httpOnly = httpOnly;
        this.secure = secure;
        this.sameSite = sameSite;
        this.maxAge = maxAge;
        this.expiry = expiry;
        this.comment = comment;
        this.additionalProperties = additionalProperties;
    }

    public void addToResponse(HttpServletResponse response) {
        response.addHeader("Set-Cookie", toHeaderString());
    }

    public String toHeaderString() {
        final StringBuilder b = new StringBuilder();

        StringBuilderUtils.appendQuotedIfWhitespace(b, getName());
        b.append('=');
        StringBuilderUtils.appendQuotedIfWhitespace(b, getValue());

        b.append("; Version=").append(getVersion());

        if (getComment() != null) {
            b.append("; Comment=");
            StringBuilderUtils.appendQuotedIfWhitespace(b, getComment());
        }
        if (getDomain() != null) {
            b.append("; Domain=");
            StringBuilderUtils.appendQuotedIfWhitespace(b, getDomain());
        }
        if (getPath() != null) {
            b.append("; Path=");
            StringBuilderUtils.appendQuotedIfWhitespace(b, getPath());
        }
        if (getMaxAge() != null) {
            b.append("; Max-Age=");
            b.append(getMaxAge());
        }
        if (isSecure()) {
            b.append("; Secure");
        }
        if (isHttpOnly()) {
            b.append("; HttpOnly");
        }
        if (getSameSite() != null) {
            switch (getSameSite()) {
                case NONE:
                    b.append("; SameSite=None");
                    break;
                case STRICT:
                    b.append("; SameSite=Strict");
                    break;
                case LAX:
                    b.append("; SameSite=Lax");
                    break;
            }
        }
        if (getExpiry() != null) {
            b.append("; Expires=");
            b.append(HttpDateFormat.getPreferredDateFormat().format(Date.from(getExpiry())));
        }
        if (getAdditionalProperties().size() != 0) {
            getAdditionalProperties().forEach((key, value) -> {
                b.append("; ");
                StringBuilderUtils.appendQuotedIfWhitespace(b, key);
                b.append("=");
                StringBuilderUtils.appendQuotedIfWhitespace(b, value);
            });
        }

        return b.toString();
    }

    public static class RealCookieBuilder {
        private long version = 1;
        private Map<String, String> additionalProperties = Maps.newHashMap();

        public RealCookieBuilder value(@NonNull String value) {
            this.value = value;
            return this;
        }

        public RealCookieBuilder value(@NonNull UserAuthCookie cookie) {
            this.value = AuthCookieUtil.encode(cookie);
            return this;
        }

        public RealCookieBuilder value(@NonNull AccountAuthCookie cookie) {
            this.value = AuthCookieUtil.encode(cookie);
            return this;
        }

        public RealCookieBuilder maxAge(Long maxAge) {
            // For backwards compatibility, also set expiry
            if (maxAge != null && maxAge != 0) {
                this.expiry = Instant.now().plus(maxAge, ChronoUnit.SECONDS);
            }
            this.maxAge = maxAge;
            return this;
        }

        public RealCookieBuilder expiry(Instant expiry) {
            this.expiry = expiry;
            return this;
        }

        public RealCookieBuilder addAdditionalProperty(String key, String value) {
            this.additionalProperties.put(key, value);
            return this;
        }

        public RealCookie build() {
            return new RealCookie(name, value, path, version, domain, httpOnly, secure, sameSite, maxAge, expiry, comment, ImmutableMap.copyOf(additionalProperties));
        }
    }
}