package e2e.io.ticktok.broadcast;

import e2e.io.ticktok.broadcast.support.AppDriver;
import e2e.io.ticktok.broadcast.support.ClockConsumer;
import org.junit.Test;

import static io.ticktok.broadcast.ClocksController.CLOCK_EXPR;

public class ApplicationE2ETest {

    private final AppDriver app = new AppDriver();
    private final ClockConsumer client = new ClockConsumer();

    @Test
    public void sendScheduledMessage() throws Exception {
        app.registeredFor(CLOCK_EXPR);
        client.receiveTheClock(CLOCK_EXPR);
    }
}