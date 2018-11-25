package test.io.ticktok.server.clock;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.Schedule;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class ScheduleTest {

    @Test
    void createSchedule() {
        Schedule schedule = Schedule.createFrom("every.1.seconds", 111L);
        assertThat(schedule.getSchedule(), is("every.1.seconds"));
        assertThat(schedule.getLatestScheduledTick(), is(111L));
        assertThat(schedule.getClockCount(), is(1));
    }
}