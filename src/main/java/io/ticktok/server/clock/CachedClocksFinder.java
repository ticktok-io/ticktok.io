package io.ticktok.server.clock;

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CachedClocksFinder implements ClocksFinder {
    private final ClocksFinder clocksFinder;

    private final Cache<Map<String, String>, List<Clock>> cache;

    public CachedClocksFinder(ClocksFinder clocksFinder, int ttl) {
        this.clocksFinder = clocksFinder;
        cache = createCache(ttl);
    }

    private Cache<Map<String, String>, List<Clock>> createCache(int ttl) {
        return new Cache2kBuilder<Map<String, String>, List<Clock>>() {}
            .expireAfterWrite(ttl, TimeUnit.SECONDS)
            .resilienceDuration(30, TimeUnit.SECONDS)
            .loader(this::findClocks)
            .build();
    }

    private List<Clock> findClocks(Map<String, String> params) {
        return clocksFinder.findBy(params);
    }

    @Override
    public List<Clock> findBy(Map<String, String> params) {
        return cache.get(params);
    }
}
