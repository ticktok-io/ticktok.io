package e2e.test.io.ticktok.server;

import e2e.test.io.ticktok.server.support.AppDriver
import e2e.test.io.ticktok.server.support.AppDriver.ClockMatcher.Companion.containsClock
import e2e.test.io.ticktok.server.support.TickListener
import e2e.test.io.ticktok.server.support.TickListener.Companion.CLOCK_EXPR
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

@TestInstance(PER_CLASS)
class ApplicationE2ETest {

    private val app = AppDriver()
    private val client = TickListener();

    @BeforeAll
    fun setUp() {
        app.start()
    }

    @Test
    internal fun registerANewClock() {
        app.registeredAClock(CLOCK_EXPR)
        app.retrievedRegisteredClock(CLOCK_EXPR)
    }

    @Test
    fun retrieveScheduledMessage() {
        val clock = app.registeredAClock(CLOCK_EXPR)
        client.receivedTicksFor(clock)
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
        val clock2 = app.registeredAClock("every.10.seconds");

        app.clocks(containsClock(clock1))
        app.clocks(containsClock(clock2))
    }

    @Test
    fun deleteAClock() {
        val clock = app.registeredAClock("every.9.seconds")
        app.deleteClock(clock)
        app.clocks(not(containsClock(clock)))
    }

    @Test
    fun failOnNonValidSchedule() {
        app.registeredAClock("non-valid")
        app.retrievedUserError();
    }

    @AfterEach
    fun resetApp() {
        app.reset()
    }
}


