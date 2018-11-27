package io.ticktok.server.tick;

public interface TickChannelCreator {

    TickChannel create(String name, String schedule);
}
