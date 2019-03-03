package test.io.ticktok.server.tick.http;

import io.ticktok.server.tick.http.HttpQueuesRepository;
import io.ticktok.server.tick.http.QueuesController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import test.io.ticktok.server.support.IntegrationTest;

import static io.ticktok.server.tick.http.HttpConfiguration.popPathForId;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = QueuesController.class, secure = false)
@ContextConfiguration(classes = {QueuesControllerTest.TestConfiguration.class})
@ActiveProfiles({"http"})
@IntegrationTest
class QueuesControllerTest {

    @Configuration
    @ComponentScan(basePackages = "io.ticktok.server.tick.http")
    public static class TestConfiguration {
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HttpQueuesRepository httpQueuesRepository;

    @Test
    void failOnNonExistingQueue() throws Exception {
       when(httpQueuesRepository.pop("tutu")).thenThrow(HttpQueuesRepository.QueueNotExistsException.class);
       mockMvc.perform(get(popPathForId("tutu")))
               .andExpect(status().isNotFound());
    }

}