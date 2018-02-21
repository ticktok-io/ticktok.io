package e2e.io.ticktok.broadcast;

import org.junit.Test;

public class ApplicationE2ETest {

    private final AppDriver app = new AppDriver();
    private final ClockConsumer client = new ClockConsumer();

    @Test
    public void delegateScheduledMessageToQueue() throws Exception {
        app.registeredFor("once.in.2.second");
        client.receivedClock();
    }
}