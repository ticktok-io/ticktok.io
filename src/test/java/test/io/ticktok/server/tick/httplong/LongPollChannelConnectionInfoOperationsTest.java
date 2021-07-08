package test.io.ticktok.server.tick.httplong;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.ChannelConnectionInfo;
import io.ticktok.server.tick.httplong.ChannelsRepository;
import io.ticktok.server.tick.httplong.LongPollTickChannelOperations;
import io.ticktok.server.tick.httplong.TicksChannel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LongPollChannelConnectionInfoOperationsTest {


    private final ChannelsRepository channelsRepository = mock(ChannelsRepository.class);
    private final LongPollTickChannelOperations tickChannelOperations =
            new LongPollTickChannelOperations(channelsRepository);

    @Test
    void retrieveGeneratedChannelId() {
        Clock clock = Clock.builder().id("123").name("popov").schedule("every.2.minutes").build();
        when(channelsRepository.createFor(clock.getId(), clock.getSchedule())).thenReturn(
                new TicksChannel(clock.getId(), "externalId", clock.getSchedule()));
        ChannelConnectionInfo channelInfo =
                tickChannelOperations.create(clock);
        assertThat(channelInfo.getDetails().get("channelId")).isEqualTo("externalId");
    }
}