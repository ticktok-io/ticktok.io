package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.support.Client
import org.junit.jupiter.api.Test

abstract class CommonBrokerTest : CommonAppE2ETest() {

    @Test
    fun retrieveScheduledTicks() {
        // Given
        val clock = app.registeredAClock("kuku", Client.CLOCK_EXPR)
        client.startListenTo(clock)
        // Expect
        client.receivedTicksFor(clock)
    }
}