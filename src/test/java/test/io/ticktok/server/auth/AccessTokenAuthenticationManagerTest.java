package test.io.ticktok.server.auth;

import io.ticktok.server.auth.AccessTokenAuthenticationManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.*;

public class AccessTokenAuthenticationManagerTest {

    public static final String TOKEN = "ab1313rEw";

    private Authentication authentication = mock(Authentication.class);
    private AccessTokenAuthenticationManager authManager = new AccessTokenAuthenticationManager(TOKEN);

    @Test
    public void shouldFailToAuthenticateNonMatchedToken() {
        when(authentication.getPrincipal()).thenReturn("non-match");
        Assertions.assertThrows(BadCredentialsException.class, () -> authManager.authenticate(authentication));
    }

    @Test
    public void shouldAuthenticateOnMatchedToken() {
        when(authentication.getPrincipal()).thenReturn(TOKEN);
        authManager.authenticate(authentication);
        verify(authentication).setAuthenticated(true);
    }

}