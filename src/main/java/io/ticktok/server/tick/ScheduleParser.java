package io.ticktok.server.tick;

import java.time.Clock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScheduleParser {

    public static final Pattern ONCE_IN_PATTERN = Pattern.compile("once\\.in\\.(\\d+)\\.seconds");

    private final String schedule;

    public ScheduleParser(String schedule) {
        this.schedule = schedule;
    }

    public long nextTickTime() {
        Matcher matcher = ONCE_IN_PATTERN.matcher(schedule);
        if(!matcher.find()) {
            throw new ExpressionNotValidException("I can't understand the schedule: " + schedule);
        }
        return now() + toMillis(Integer.valueOf(matcher.group(1)));
    }

    protected long now() {
        return Clock.systemUTC().millis();
    }

    private long toMillis(int minutes) {
        return minutes * 1000;
    }

    public static class ExpressionNotValidException extends RuntimeException {
        public ExpressionNotValidException(String message) {
            super(message);
        }
    }
}
