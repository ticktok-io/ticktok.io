package io.ticktok.server.tick.http;

import com.google.common.collect.ImmutableMap;
import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.QueueNameCreator;
import io.ticktok.server.tick.TickChannel;
import io.ticktok.server.tick.TickChannelExplorer;

import static io.ticktok.server.tick.http.HttpConfiguration.POP_PATH;


public class HttpTickChannelExplorer implements TickChannelExplorer {

    private final HttpQueuesRepository queuesRepository;

    public HttpTickChannelExplorer(HttpQueuesRepository queuesRepository) {
        this.queuesRepository = queuesRepository;
    }

    @Override
    public boolean isExists(Clock clock) {
        return queuesRepository.isQueueExists(queueNameFor(clock));
    }

    private String queueNameFor(Clock clock) {
        return new QueueNameCreator(clock).create();
    }

    @Override
    public TickChannel create(Clock clock) {
        HttpQueue httpQueue = queuesRepository.createQueue(queueNameFor(clock), clock.getSchedule());
        return TickChannel.builder()
                .type(TickChannel.HTTP)
                .details(ImmutableMap.of("path", POP_PATH.replaceAll("\\{id}", httpQueue.getId())))
                .build();
    }

    @Override
    public void disable(Clock clock) {
        queuesRepository.updateQueueSchedule(queueNameFor(clock), "");
    }

    @Override
    public void enable(Clock clock) {
        queuesRepository.updateQueueSchedule(queueNameFor(clock), clock.getSchedule());
    }
}
