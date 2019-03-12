package io.ticktok.server.schedule;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

public class ScheduleParser {

    public static final String MATCH_UNITS = "seconds|hours";
    public static final Pattern EVERY_PATTERN = Pattern.compile(format("^every\\.(\\d+)\\.(%s)$", MATCH_UNITS));

    private static final Map<String, Integer> mapUnitToSeconds = ImmutableMap.of(
            "seconds", 1,
            "hours", 60 * 60
    );

    private final String schedule;

    public ScheduleParser(String schedule) {
        this.schedule = schedule;
    }

    public int interval() {
        Matcher matcher = getMatcher();
        return intervalFrom(matcher) * mapUnitToSeconds.get(matcher.group(2));
    }

    private Integer intervalFrom(Matcher matcher) {
        Integer i = Integer.valueOf(matcher.group(1));
        failIf(() -> i == 0, "Interval of 0 yis not supported");
        return i;
    }

    private void failIf(BooleanSupplier predicate, String errorMsg) {
        if(predicate.getAsBoolean()) {
            throw new ExpressionNotValidException(errorMsg);
        }
    }

    private Matcher getMatcher() {
        Matcher matcher = EVERY_PATTERN.matcher(schedule);
        failIf(() -> !matcher.find(), "Unable to understand the schedule: " + schedule);
        return matcher;
    }

    public static class ExpressionNotValidException extends RuntimeException {
        public ExpressionNotValidException(String message) {
            super(message);
        }
    }
}
