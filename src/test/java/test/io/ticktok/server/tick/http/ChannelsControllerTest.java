package test.io.ticktok.server.tick.http;

import com.google.gson.Gson;
import io.ticktok.server.tick.httplong.ChannelsController;
import io.ticktok.server.tick.httplong.ChannelsRepository;
import io.ticktok.server.tick.httplong.PollRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import test.io.ticktok.server.support.IntegrationTest;

import static io.ticktok.server.tick.httplong.LongPollConfiguration.POLL_PATH;
import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ChannelsController.class)
@ContextConfiguration(classes = {ChannelsControllerTest.TestConfiguration.class})
@ActiveProfiles({"http-long"})
@IntegrationTest
class ChannelsControllerTest {
    @Configuration
    @ComponentScan(basePackages = "io.ticktok.server.tick.httplong")
    public static class TestConfiguration {
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChannelsRepository channelsRepository;

    @WithMockUser("spring")
    @Test
    void failOnUnknownChannel() throws Exception {
        String unknownChannelId = "unknown";
        doThrow(ChannelsRepository.ChannelNotExistsException.class)
                .when(channelsRepository).updateLastPollTime(eq(asList(unknownChannelId)), anyLong());
        MvcResult result = mockMvc.perform(
                post(POLL_PATH)
                        .content(new Gson().toJson(new PollRequest(unknownChannelId)))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
        ).andReturn();
        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isBadRequest());
    }
}