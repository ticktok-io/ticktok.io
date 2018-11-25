package io.ticktok.server.tick;

import io.ticktok.server.tick.rabbit.QueueNameCreator;

public interface TickChannelExplorer {
    boolean isExists(String name);
}
