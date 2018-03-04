package io.ticktok.broadcast.http;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class AccessTokenAuthenticationManager implements AuthenticationManager {
    private final String authToken;

    public AccessTokenAuthenticationManager(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if(!authToken.equals(authentication.getPrincipal())) {
            throw new BadCredentialsException("The access token was not found or is not the expected value.");
        }
        authentication.setAuthenticated(true);
        return authentication;
    }
}
