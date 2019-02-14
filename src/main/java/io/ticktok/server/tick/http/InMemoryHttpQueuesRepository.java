package io.ticktok.server.tick.http;

import io.ticktok.server.tick.TickMessage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryHttpQueuesRepository implements HttpQueuesRepository {
    private Map<String, List<String>> scheduleToClockMap = new HashMap<>();
    private Map<String, List<TickMessage>> ticks = new ConcurrentHashMap<>(new HashMap<>());

    @Override
    public List<TickMessage> pop(String clockId) {
        List<TickMessage> tickMessages = ticks.computeIfAbsent(clockId, (k) -> new ArrayList<>());
        ticks.remove(clockId);
        return tickMessages;
    }

    @Override
    public void add(String schedule) {
        scheduleToClockMap.getOrDefault(schedule, new ArrayList<>()).forEach(c -> {
            addArrayItem(ticks, c, new TickMessage(schedule));
        });
    }

    private void addArrayItem(Map map, String key, Object item) {
        List list = (List) map.getOrDefault(key, new ArrayList());
        list.add(item);
        map.put(key, list);

    }

    @Override
    public void assignClock(String clockId, String schedule) {
        addArrayItem(scheduleToClockMap, schedule, clockId);
    }
}
