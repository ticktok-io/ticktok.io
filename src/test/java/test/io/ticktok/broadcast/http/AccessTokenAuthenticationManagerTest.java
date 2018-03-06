package test.io.ticktok.broadcast.http;

import io.ticktok.broadcast.auth.AccessTokenAuthenticationManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.*;

public class AccessTokenAuthenticationManagerTest {

    private static final String SHA1_1313 = "23b36ea4f70670ae377a591fdc03d36a9bebb481";

    private Authentication authentication = mock(Authentication.class);
    private AccessTokenAuthenticationManager authManager = new AccessTokenAuthenticationManager(SHA1_1313);

    @Test
    public void shouldFailToAuthenticateNonMatchedToken() {
        when(authentication.getPrincipal()).thenReturn("non-match");
        Assertions.assertThrows(BadCredentialsException.class, () -> authManager.authenticate(authentication));
    }

    @Test
    public void shouldAuthenticateOnMatchedToken() {
        when(authentication.getPrincipal()).thenReturn("1313");
        authManager.authenticate(authentication);
        verify(authentication).setAuthenticated(true);
    }

}