package test.io.ticktok.server.clock;

import com.google.common.collect.ImmutableMap;
import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.ClockResourceV2;
import io.ticktok.server.tick.TickChannel;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;

class ClockResourceV2Test {

    public static final String DOMAIN = "http://my-domain";
    public static final Clock CLOCK = new Clock("3264", "kukula", "every.11.seconds", Clock.PAUSED, 243);


    @Test
    void delegateClock() {
        ClockResourceV2 resource = ClockResourceV2.builder()
                .domain(DOMAIN)
                .clock(CLOCK)
                .build();
        assertThat(resource.getClockId()).isEqualTo(CLOCK.getId());
        assertThat(resource.getName()).isEqualTo(CLOCK.getName());
        assertThat(resource.getSchedule()).isEqualTo(CLOCK.getSchedule());
        assertThat(resource.getStatus()).isEqualTo(CLOCK.getStatus());
    }

    @Test
    void replaceDomainPlaceHolder() {

        ClockResourceV2 resource = createResourceWithChannelDetails("kuku", "{domain}/hello");
        assertThat(resource.getChannel().getDetails().get("kuku")).isEqualTo("http://my-domain/hello");
    }

    @NotNull
    private ClockResourceV2 createResourceWithChannelDetails(String key, String value) {
        return ClockResourceV2.builder()
                .domain(DOMAIN)
                .clock(new Clock())
                .channel(TickChannel.builder().details(ImmutableMap.of(key, value)).build())
                .build();
    }

    @Test
    void shouldNotReplaceUnknownPlaceHolders() {
        ClockResourceV2 resource = createResourceWithChannelDetails("popo", "{unknown}/hello");
        assertThat(resource.getChannel().getDetails().get("popo")).isEqualTo("{unknown}/hello");
    }

    @Test
    void shouldDelegateAllChannelDetails() {
        final TickChannel channel = TickChannel.builder()
                .type("rabbit")
                .queue("q1")
                .uri("amqp://uri")
                .details(ImmutableMap.of("k", "v")).build();
        ClockResourceV2 resource = ClockResourceV2.builder()
                .domain(DOMAIN)
                .clock(new Clock())
                .channel(channel)
                .build();
        assertThat(resource.getChannel()).isEqualTo(channel);
    }

}