package test.io.ticktok.server.schedule;

import io.ticktok.server.schedule.Schedule;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class ScheduleTest {

    @Test
    void createSchedule() {
        Schedule schedule = Schedule.createFrom("every.1.seconds", 111L);
        assertThat(schedule.getSchedule(), is("every.1.seconds"));
        assertThat(schedule.getNextTick(), is(111L));
        assertThat(schedule.getClockCount(), is(1));
    }
}