package io.ticktok.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;

@Configuration
@EnableRetry
@EnableScheduling
public class ApplicationConfig {

    @Bean
    public Clock systemClock() {
        return Clock.systemUTC();
    }
}
