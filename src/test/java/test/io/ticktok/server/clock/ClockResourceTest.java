package test.io.ticktok.server.clock;

import com.google.common.collect.ImmutableMap;
import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.control.ClockResource;
import io.ticktok.server.tick.ChannelConnectionInfo;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClockResourceTest {

    public static final String DOMAIN = "http://my-domain";
    public static final Clock CLOCK = new Clock("3264", "kukula", "every.11.seconds", Clock.PAUSED, 243);


    @Test
    void delegateClock() {
        ClockResource resource = ClockResource.builder()
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

        ClockResource resource = createResourceWithChannelDetails("kuku", "{domain}/hello");
        assertThat(resource.getChannel().getDetails().get("kuku")).isEqualTo("http://my-domain/hello");
    }

    @NotNull
    private ClockResource createResourceWithChannelDetails(String key, String value) {
        return ClockResource.builder()
                .domain(DOMAIN)
                .clock(new Clock())
                .channel(ChannelConnectionInfo.builder().details(ImmutableMap.of(key, value)).build())
                .build();
    }

    @Test
    void shouldNotReplaceUnknownPlaceHolders() {
        ClockResource resource = createResourceWithChannelDetails("popo", "{unknown}/hello");
        assertThat(resource.getChannel().getDetails().get("popo")).isEqualTo("{unknown}/hello");
    }

    @Test
    void shouldDelegateAllChannelDetails() {
        final ChannelConnectionInfo channel = ChannelConnectionInfo.builder()
                .type("rabbit")
                .queue("q1")
                .uri("amqp://uri")
                .details(ImmutableMap.of("k", "v")).build();
        ClockResource resource = ClockResource.builder()
                .domain(DOMAIN)
                .clock(new Clock())
                .channel(channel)
                .build();
        assertThat(resource.getChannel()).isEqualTo(channel);
    }

}