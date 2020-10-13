package com.smotana.clearflask.util;

import com.google.common.net.InetAddresses;
import com.smotana.clearflask.core.ServiceInjector;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.Strings;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;

/**
 * Used for intercepting registration of already registered beans. Handles it by re-registering instead of throwing.
 */
@Slf4j
public class IpUtil {
    public static String getRemoteIp(HttpServletRequest request, ServiceInjector.Environment env) {
        String remoteIp;
        switch (env) {
            case PRODUCTION_AWS:
                String xForwardedFor = request.getHeader("x-forwarded-for");
                if (Strings.isNullOrEmpty(xForwardedFor)) {
                    // Most likely originated as a LB health check or a local query bypassing LB
                    remoteIp = request.getRemoteAddr();
                } else {
                    String[] xForwardedForIps = xForwardedFor.split(",");
                    if (xForwardedForIps.length == 0) {
                        remoteIp = request.getRemoteAddr();
                    } else if (xForwardedForIps.length == 1) {
                        remoteIp = xForwardedForIps[0].trim();
                    } else {
                        remoteIp = xForwardedForIps[xForwardedForIps.length - 2].trim();
                    }
                }
                break;
            case TEST:
            case DEVELOPMENT_LOCAL:
                remoteIp = request.getRemoteAddr();
                break;
            default:
                throw new InternalServerErrorException("Unknown environment: " + env);
        }
        if (log.isTraceEnabled()) {
            log.trace("Got remote IP {} from remoteAddr {} and x-forwarded-for {}",
                    remoteIp, request.getRemoteAddr(), request.getHeader("x-forwarded-for"));
        }
        if (!InetAddresses.isInetAddress(remoteIp)) {
            throw new InternalServerErrorException("Not a valid remote IP: " + remoteIp);
        }
        return remoteIp;
    }
}
