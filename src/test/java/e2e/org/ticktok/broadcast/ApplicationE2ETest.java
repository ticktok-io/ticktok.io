package e2e.org.ticktok.broadcast;

import org.junit.Test;

import static java.lang.Thread.sleep;

public class ApplicationE2ETest {

    private static final long SECOND = 1000;

    private final TicktokClient client = new TicktokClient();

    @Test
    public void delegateScheduledMessageToQueue() throws Exception {
        client.registeredFor("once.in.1.second");
        sleep(SECOND);
        client.receivedClock();
    }
}