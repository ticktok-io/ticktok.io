package io.ticktok.server.tick;

import java.time.Clock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScheduleParser {

    public static final Pattern EVERY_PATTERN = Pattern.compile("every\\.(\\d+)\\.seconds");

    private final String schedule;

    public ScheduleParser(String schedule) {
        this.schedule = schedule;
    }

    public int interval() {
        return Integer.valueOf(getMatcher().group(1));
    }

    private Matcher getMatcher() {
        Matcher matcher = EVERY_PATTERN.matcher(schedule);
        if(!matcher.find()) {
            throw new ExpressionNotValidException("Unable to understand the schedule: " + schedule);
        }
        return matcher;
    }

    public static class ExpressionNotValidException extends RuntimeException {
        public ExpressionNotValidException(String message) {
            super(message);
        }
    }
}
