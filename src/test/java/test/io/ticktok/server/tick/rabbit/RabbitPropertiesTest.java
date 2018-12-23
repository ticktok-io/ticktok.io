package test.io.ticktok.server.tick.rabbit;

import io.ticktok.server.tick.rabbit.RabbitProperties;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class RabbitPropertiesTest {

    private final RabbitProperties properties = new RabbitProperties();

    @Test
    void retrieveConsumerUri() {
        properties.setConsumerUri("uri");
        assertThat(properties.getConsumerUri(), is("uri"));
    }

    @Test
    void fallbackToUriIfConsumerUriNotSet() {
        properties.setUri("ruri");
        assertThat(properties.getConsumerUri(), is("ruri"));
    }
}