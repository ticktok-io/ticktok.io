package io.ticktok.server.tick.rabbit;

import io.ticktok.server.tick.TickChannel;
import io.ticktok.server.tick.TickChannelFactory;

public class RabbitTickChannelFactory implements TickChannelFactory {

    private final String rabbitUri;
    private final String exchange;

    public RabbitTickChannelFactory(String rabbitUri, String exchange) {
        this.rabbitUri = rabbitUri;
        this.exchange = exchange;
    }

    @Override
    public TickChannel createForSchedule(String schedule) {
        return new TickChannel(rabbitUri, exchange, schedule);
    }
}
