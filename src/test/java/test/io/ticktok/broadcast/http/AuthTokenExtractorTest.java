package test.io.ticktok.broadcast.http;

import io.ticktok.broadcast.auth.AuthTokenExtractor;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;

import static io.ticktok.broadcast.auth.AuthTokenExtractor.ACCESS_TOKEN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthTokenExtractorTest {

    private final HttpServletRequest request = mock(HttpServletRequest.class);

    @Test
    void retrieveEmptyStringOnNoToken() {
        when(request.getParameter(ACCESS_TOKEN)).thenReturn("");
        assertThat(new AuthTokenExtractor(request).extract(), is(""));
    }

    @Test
    void retrieveAccessTokenQueryParam() {
        when(request.getParameter(ACCESS_TOKEN)).thenReturn("1122");
        assertThat(new AuthTokenExtractor(request).extract(), is("1122"));
    }
}