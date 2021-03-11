package io.ticktok.server.tick.http;

import com.google.common.collect.ImmutableMap;
import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.QueueName;
import io.ticktok.server.tick.ChannelConnectionInfo;
import io.ticktok.server.tick.TickChannelOperations;

import static io.ticktok.server.tick.http.HttpConfiguration.popPathForId;


public class HttpTickChannelOperations implements TickChannelOperations {

    public static final String URL_PARAM = "url";
    private final HttpQueuesRepository queuesRepository;

    public HttpTickChannelOperations(HttpQueuesRepository queuesRepository) {
        this.queuesRepository = queuesRepository;
    }

    @Override
    public boolean isExists(Clock clock) {
        return queuesRepository.isQueueExists(queueNameFor(clock));
    }

    private String queueNameFor(Clock clock) {
        return QueueName.createNameFor(clock);
    }

    @Override
    public ChannelConnectionInfo create(Clock clock) {
        HttpQueue httpQueue = queuesRepository.createQueue(queueNameFor(clock));
        return ChannelConnectionInfo.builder()
                .type(ChannelConnectionInfo.HTTP)
                .details(ImmutableMap.of(
                        URL_PARAM, fullUrlFor(httpQueue.getExternalId())
                )
                ).build();
    }

    private String fullUrlFor(String id) {
        return "{domain}" + popPathForId(id);
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
