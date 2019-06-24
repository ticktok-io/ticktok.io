package io.ticktok.server.schedule;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

public class ScheduleParser {

    private static final Map<String, Integer> mapUnitToSeconds = ImmutableMap.of(
            "seconds", 1,
            "minutes", 60,
            "hours", 60 * 60
    );
    public static final String MATCH_UNITS = String.join("|", mapUnitToSeconds.keySet());
    public static final Pattern EVERY_PATTERN = Pattern.compile(format("^((\\@never)|(every\\.(\\d+)\\.(%s)))$", MATCH_UNITS));


    private final String schedule;

    public ScheduleParser(String schedule) {
        this.schedule = schedule;
    }

    public int interval() {
        return intervalFrom(getMatcher()) * 1000;
    }

    private Matcher getMatcher() {
        Matcher matcher = EVERY_PATTERN.matcher(schedule);
        failIf(() -> !matcher.find(), "Unable to understand the schedule: " + schedule);
        return matcher;
    }

    private Integer intervalFrom(Matcher matcher) {
        if(matcher.group(2) != null) {
            return 0;
        }
        Integer i = Integer.valueOf(matcher.group(4));
        failIf(() -> i == 0, "Interval of 0 is not supported");
        return i * mapUnitToSeconds.get(matcher.group(5));
    }

    private void failIf(BooleanSupplier predicate, String errorMsg) {
        if(predicate.getAsBoolean()) {
            throw new ExpressionNotValidException(errorMsg);
        }
    }

    public static class ExpressionNotValidException extends RuntimeException {
        public ExpressionNotValidException(String message) {
            super(message);
        }
    }
}
