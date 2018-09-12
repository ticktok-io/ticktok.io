package test.io.ticktok.server.tick;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories({"io.ticktok.server.clock", "io.ticktok.server.tick"})
public class SpringMongoConfiguration {
}
