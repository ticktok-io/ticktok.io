package test.io.ticktok.broadcast.http;

import io.ticktok.broadcast.auth.AccessTokenAuthenticationManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.*;

public class AccessTokenAuthenticationManagerTest {

    private static final String AUTH_TOKEN = "1313";

    private Authentication authentication = mock(Authentication.class);
    private AccessTokenAuthenticationManager authManager = new AccessTokenAuthenticationManager(AUTH_TOKEN);

    @Test
    public void shouldFailToAuthenticateNonMatchedToken() {
        when(authentication.getPrincipal()).thenReturn("non-match");
        Assertions.assertThrows(BadCredentialsException.class, () -> authManager.authenticate(authentication));
    }

    @Test
    public void shouldAuthenticateOnMatchedToken() {
        when(authentication.getPrincipal()).thenReturn(AUTH_TOKEN);
        authManager.authenticate(authentication);
        verify(authentication).setAuthenticated(true);
    }

}