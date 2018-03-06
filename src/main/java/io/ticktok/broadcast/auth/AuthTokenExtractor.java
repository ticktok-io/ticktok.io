package io.ticktok.broadcast.auth;

import javax.servlet.http.HttpServletRequest;

public class AuthTokenExtractor {

    public static final String ACCESS_TOKEN = "access_token";
    private final HttpServletRequest request;

    public AuthTokenExtractor(HttpServletRequest request) {
        this.request = request;

    }

    public Object extract() {
        return request.getParameter(ACCESS_TOKEN);
    }
}
