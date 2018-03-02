package e2e.io.ticktok.broadcast;

import e2e.io.ticktok.broadcast.support.AppDriver;
import e2e.io.ticktok.broadcast.support.ClockConsumer;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.ticktok.broadcast.ClocksController.CLOCK_EXPR;

public class ApplicationE2ETest {

    private static final AppDriver app = new AppDriver();
    private final ClockConsumer client = new ClockConsumer();

    @BeforeClass
    public static void setUp() throws Exception {
        app.start();
    }

    @Test
    public void sendScheduledMessage() throws Exception {
        app.registerFor(CLOCK_EXPR);
        client.receivedTheClock(CLOCK_EXPR);
    }

    @Test
    public void shouldBeHealthy() throws Exception {
        app.isHealthy();
    }
}