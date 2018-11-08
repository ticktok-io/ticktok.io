package test.io.ticktok.server.tick;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@Configuration
@EnableMongoRepositories({"io.ticktok.server.clock", "io.ticktok.server.tick"})
@ComponentScan(basePackages = {"io.ticktok.server.clock.repository"})
public class SpringMongoConfiguration {

    public static final Instant FIXED_INSTANT = Instant.parse("2018-01-01T10:15:00.00Z");

    @Bean
    public Clock systemClock() {
        return Clock.fixed(FIXED_INSTANT, ZoneId.of("UTC"));
    }

}
