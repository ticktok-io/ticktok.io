package test.io.ticktok.server.clock;

import com.google.common.collect.ImmutableMap;
import io.ticktok.server.clock.CachedClocksFinder;
import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.ClocksFinder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CachedClocksFinderTest {

    static final List<Clock> CLOCKS = Arrays.asList(new Clock("lala", "every.12.seconds"));
    static final ImmutableMap<String, String> PARAMS = ImmutableMap.of("name", "lala");
    private static final int TTL = 1;

    final ClocksFinder clocksFinder = mock(ClocksFinder.class);
    final CachedClocksFinder cachedClocksFinder = new CachedClocksFinder(clocksFinder, TTL);

    @Test
    void delegateParamsToFinder() {
        cachedClocksFinder.findBy(PARAMS);
        verify(clocksFinder).findBy(PARAMS);
    }

    @Test
    void delegateResultFromFinder() {
        when(clocksFinder.findBy(PARAMS)).thenReturn(CLOCKS);
        assertThat(cachedClocksFinder.findBy(PARAMS)).isEqualTo(CLOCKS);
    }

    @Test
    void shouldNotDelegateASecondTimeOnConsecutiveCalls() {
        List<List<Clock>> results = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            results.add(cachedClocksFinder.findBy(PARAMS));
        }
        verify(clocksFinder, times(1)).findBy(PARAMS);
        assertThat(results.stream().allMatch(e -> e.equals(results.get(0)))).isTrue();
    }

    @Test
    void shouldExpireCache() throws InterruptedException {
        cachedClocksFinder.findBy(PARAMS);
        Thread.sleep(TTL * 1000 + 100);
        cachedClocksFinder.findBy(PARAMS);
        verify(clocksFinder, times(2)).findBy(PARAMS);
    }
}