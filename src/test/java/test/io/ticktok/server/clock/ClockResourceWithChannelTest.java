package test.io.ticktok.server.clock;

import com.google.common.collect.ImmutableMap;
import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.ClockResourceWithChannel;
import io.ticktok.server.tick.TickChannel;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClockResourceWithChannelTest {

    public static final String DOMAIN = "http://my-domain";

    @Test
    void replaceDomainPlaceHolder() {
        ClockResourceWithChannel resource = createResourceWithChannelDetails("kuku", "{domain}/hello");
        assertThat(resource.getChannel().getDetails().get("kuku")).isEqualTo("http://my-domain/hello");
    }

    @NotNull
    private ClockResourceWithChannel createResourceWithChannelDetails(String key, String value) {
        return new ClockResourceWithChannel(
                DOMAIN,
                new Clock(),
                TickChannel.builder().details(ImmutableMap.of(key, value)).build());
    }

    @Test
    void shouldNotReplaceUnknownPlaceHolders() {
        ClockResourceWithChannel resource = createResourceWithChannelDetails("popo", "{unknown}/hello");
        assertThat(resource.getChannel().getDetails().get("popo")).isEqualTo("{unknown}/hello");
    }

    @Test
    void shouldDelegateAllChannelDetails() {
        final TickChannel channel = TickChannel.builder()
                .type("rabbit")
                .queue("q1")
                .uri("amqp://uri")
                .details(ImmutableMap.of("k", "v")).build();
        ClockResourceWithChannel resource = new ClockResourceWithChannel(DOMAIN, new Clock(), channel);
        assertThat(resource.getChannel()).isEqualTo(channel);
    }
}