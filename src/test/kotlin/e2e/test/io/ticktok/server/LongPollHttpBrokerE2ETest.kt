package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.support.App
import e2e.test.io.ticktok.server.support.App.Companion.HTTP
import e2e.test.io.ticktok.server.support.Client
import e2e.test.io.ticktok.server.support.LongPollConsumer
import e2e.test.io.ticktok.server.support.TickConsumer
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("http-broker")
class LongPollHttpBrokerE2ETest : CommonAppE2ETest() {

    override fun appProfile(): String {
        return HTTP
    }

    override fun tickConsumer(): TickConsumer {
        return LongPollConsumer(App.APP_URL, App.ACCESS_TOKEN)
    }

    @Test
    fun retrieveScheduledTicks() {
        // Given
        val clock1 = app.registeredAClock("kuku", Client.CLOCK_EXPR)
        val clock2 = app.registeredAClock("popov", Client.CLOCK_EXPR)
        client.startListenTo(clock1, clock2)
        // Expect
        client.receivedTicksFor(clock1, clock2)
    }

    @Test
    fun noTicksWhenNothingIsSchedules() {
        val clock = app.registeredAClock("kuku", "@never")
        client.startListenTo(clock)
        client.receivedNoTicks()
    }


}