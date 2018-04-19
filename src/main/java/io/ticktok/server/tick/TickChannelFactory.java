package io.ticktok.server.tick;

public interface TickChannelFactory {

    TickChannel createForSchedule(String schedule);
}
