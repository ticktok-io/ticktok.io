package io.ticktok.server.tick.http;

import io.ticktok.server.tick.TickChannelExplorer;
import io.ticktok.server.tick.TickMessage;
import io.ticktok.server.tick.TickPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Configuration
@Profile("http")
public class HttpConfiguration {

    public static final java.lang.String POP_PATH = "/api/v1/queues/{id}/pop";

    @Bean
    public TickChannelExplorer tickChannelExplorer() {
        return new HttpTickChannelExplorer();
    }

    @Bean
    public TickPublisher tickPublisher(HttpQueuesRepository repository) {
        return new HttpTickPublisher(repository, clocksRepository);
    }

    @Bean
    public HttpQueuesRepository queuesRepository(MongoOperations mongoOperations) {
        return new MongoHttpQueuesRepository(mongoOperations);
    }

    @RestController
    public static class QueuesController {
        private HttpQueuesRepository httpQueuesRepository;

        @GetMapping(HttpConfiguration.POP_PATH)
        public List<TickMessage> pop(@PathVariable String id) {
            return httpQueuesRepository.pop(id);
        }
    }
}
