package test.io.ticktok.server.auth;

import io.ticktok.server.auth.NoTokenServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.ServletRequest;

import static io.ticktok.server.auth.AuthTokenExtractor.AUTH_PARAM;
import static org.assertj.core.api.Assertions.assertThat;

class NoTokenServletRequestTest {

    MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    void removeAccessTokenParameterFromRequest() throws Exception {
        request.addParameter(AUTH_PARAM, new String[]{"token"});
        ServletRequest newRequest = new NoTokenServletRequest(request);
        assertThat(newRequest.getParameter(AUTH_PARAM)).isNull();
        assertThat(newRequest.getParameterMap().get(AUTH_PARAM)).isNull();
    }

    @Test
    void leaveOtherParamsTheSame() {
        request.addParameter("name", new String[]{"laklak"});
        request.addParameter("schedule", new String[]{"@never"});
        ServletRequest newRequest = new NoTokenServletRequest(request);
        assertThat(newRequest.getParameter("name")).isEqualTo("laklak");
        assertThat(newRequest.getParameterMap().get("schedule")[0]).isEqualTo("@never");
    }
}