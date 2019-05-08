package io.ticktok.server.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import static io.ticktok.server.auth.AuthTokenExtractor.AUTH_PARAM;
import static java.util.stream.Collectors.toMap;

public class NoTokenServletRequest extends HttpServletRequestWrapper {

    private final Map<String, String[]> parameters;

    public NoTokenServletRequest(HttpServletRequest originalRequest) {
        super(originalRequest);

        parameters = originalRequest.getParameterMap().entrySet().stream()
                .filter(e -> !e.getKey().equals(AUTH_PARAM))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameters;
    }

    @Override
    public String getParameter(String name) {
        final String[] values = parameters.get(name);
        return values != null ? values[0] : null;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return parameters.get(name);
    }
}
