package io.ticktok.server.clock.control;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

public class HttpRequestUtil {

    static public String host() {
        HttpServletRequest currentRequest = currentRequest();
        return currentRequest.getRequestURL().toString().replaceAll(currentRequest.getRequestURI(), "");
    }

    static private HttpServletRequest currentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
    }

    static public Principal userPrincipal() {
        return currentRequest().getUserPrincipal();
    }

    static public String pathParam(String name) {
        final Map<String, String> attributes =
                (Map<String, String>) currentRequest().getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        return attributes.get(name);
    }
}
