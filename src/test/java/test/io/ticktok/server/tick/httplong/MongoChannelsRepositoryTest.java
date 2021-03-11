package test.io.ticktok.server.tick.httplong;

import io.ticktok.server.tick.httplong.ChannelsRepository;
import io.ticktok.server.tick.httplong.TicksChannel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test.io.ticktok.server.support.IntegrationTest;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@DataMongoTest
@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MongoChannelsRepositoryTest.TestConfiguration.class})
@ActiveProfiles({"http-long"})
@IntegrationTest
class MongoChannelsRepositoryTest {


    @Configuration
    @ComponentScan(basePackages = {"io.ticktok.server.tick.httplong"})
    static class TestConfiguration {
    }

    @Autowired
    private ChannelsRepository repository;

    @Test
    void succeedUpdateLastPollTimeForExistingChannel() {
        TicksChannel channel = repository.createFor("clockId", "schedule");
        repository.updateLastPollTime(Arrays.asList(channel.getKey()));
        // pass
    }

    @Test
    void failOnNonExistingChannel() {
        TicksChannel channel = repository.createFor("clockId", "schedule");
        assertThatThrownBy(() ->
                repository.updateLastPollTime(Arrays.asList(channel.getKey(), "invalidChannelId")))
                .isInstanceOf(ChannelsRepository.ChannelNotExistsException.class);
    }

    @Test
    void retrieveSameChannelForSameClock() {
        String clockId = "kuku";
        assertThat(repository.createFor(clockId, "every.1.seconds")).isEqualTo(
                repository.createFor(clockId, "every.1.seconds"));
    }

    @Test
    void generateDifferentKeyForDifferentClocks() {
        TicksChannel channel1 = repository.createFor("clock1", "schedule");
        TicksChannel channel2 = repository.createFor("clock2", "schedule");
        assertThat(channel1.getId()).isNotEqualTo(channel2.getId());
        assertThat(channel1.getKey()).isNotEqualTo(channel2.getKey());
    }
}