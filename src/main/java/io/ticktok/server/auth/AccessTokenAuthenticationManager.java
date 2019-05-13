package io.ticktok.server.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AccessTokenAuthenticationManager implements AuthenticationManager {
    private final String authToken;

    public AccessTokenAuthenticationManager(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        validateToken((String) authentication.getPrincipal());
        authentication.setAuthenticated(true);
        return authentication;
    }

    private void validateToken(String token) {
        if(!authToken.equals(token)) {
            throw new BadCredentialsException("The access token was not found or is not the expected value.");
        }
    }

    private String sha1(String input) {
        String sha1 = null;
        try {
            MessageDigest msdDigest = MessageDigest.getInstance("SHA-1");
            msdDigest.update(input.getBytes(StandardCharsets.UTF_8), 0, input.length());
            sha1 = DatatypeConverter.printHexBinary(msdDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            //ignore
        }
        return sha1;
    }
}
