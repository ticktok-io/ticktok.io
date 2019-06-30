package io.ticktok.server.clock;

import java.util.List;
import java.util.Map;

public interface ClocksFinder {

    List<Clock> findBy(Map<String, String> params);

}
