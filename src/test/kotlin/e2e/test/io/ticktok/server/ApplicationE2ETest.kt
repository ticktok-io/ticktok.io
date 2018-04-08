package e2e.test.io.ticktok.server;

import e2e.test.io.ticktok.server.support.AppDriver
import e2e.test.io.ticktok.server.support.AppDriver.ClockMatcher.Companion.containsClock
import e2e.test.io.ticktok.server.support.ClockClient
import io.ticktok.server.clock.ClocksController.CLOCK_EXPR
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

@TestInstance(PER_CLASS)
class ApplicationE2ETest {

    private val app = AppDriver()
    private val client = ClockClient();

    @BeforeAll
    fun setUp() {
        app.start()
    }

    @Test
    fun sendScheduledMessage() {
        val clock = app.registeredAClock(CLOCK_EXPR)
        app.retrievedRegisteredClock(CLOCK_EXPR)
        client.receivedTickFor(clock)
    }

    @Test
    fun shouldBeHealthy() {
        app.isHealthy()
    }

    @Test
    fun failWhenTokenNotProvided() {
        app.isAccessedWithoutAToken()
        app.retrieveAuthError()
    }

    @Test
    fun retrieveConfiguredClocks() {
        val clock1 = app.registeredAClock("every.6.seconds");
        val clock2 = app.registeredAClock("every.1.minute");

        app.clocks(containsClock(clock1))
        app.clocks(containsClock(clock2))
    }

    @Test
    fun deleteAClock() {
        val clock = app.registeredAClock("every.9.seconds")
        app.deleteClock(clock)
        app.clocks(not(containsClock(clock)))
    }

}


