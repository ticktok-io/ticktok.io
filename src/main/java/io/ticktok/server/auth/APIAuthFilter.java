package io.ticktok.server.auth;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;

public class APIAuthFilter extends AbstractPreAuthenticatedProcessingFilter {

    public APIAuthFilter(String authToken) {
        setAuthenticationManager(new AccessTokenAuthenticationManager(authToken));
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return new AuthTokenExtractor(request).extract();
    }



    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }
}
