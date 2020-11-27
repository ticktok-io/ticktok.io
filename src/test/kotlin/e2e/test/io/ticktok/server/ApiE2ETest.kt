package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.support.AppDriver.Companion.RABBIT
import e2e.test.io.ticktok.server.support.Client.Companion.CLOCK_EXPR
import e2e.test.io.ticktok.server.support.Clock
import e2e.test.io.ticktok.server.support.ClockMatcher.Companion.containsClock
import e2e.test.io.ticktok.server.support.ClockMatcher.Companion.containsOnly
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.lang.Thread.sleep

class ApiE2ETest : CommonAppE2ETest() {

    override fun appProfile(): String {
        return RABBIT
    }

    @Test
    fun registerNewClock() {
        app.registeredAClock("kuku", CLOCK_EXPR)
        app.retrievedRegisteredClock("kuku", CLOCK_EXPR)
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
        val clock1 = app.registeredAClock("kuku6", "every.6.seconds")
        val clock2 = app.registeredAClock("popo10", "every.10.seconds")

        app.clocks(containsClock(clock1.copy(status = Clock.ACTIVE)))
        app.clocks(containsClock(clock2.copy(status = Clock.ACTIVE)))
    }

    @Test
    fun purgeClocksWithNoConsumers() {
        val clock = app.registeredAClock("purger", CLOCK_EXPR)
        sleep(1500)
        app.clocks(not(containsClock(clock)))
    }

    @Test
    fun failOnInValidSchedule() {
        app.registeredAClock("kuku", "non-valid")
        app.retrievedUserError()
    }

    @RepeatedTest(value = 2, name = "handleConcurrentClockRequests {currentRepetition}/{totalRepetitions}")
    fun handleConcurrentClockRequests() {
        app.purge()
        sleep(500)
        invokeMultipleClockRequestsInParallel()
        app.allInteractionsSucceeded()
    }

    private fun invokeMultipleClockRequestsInParallel() {
        val threads = (0..5).map {
            Thread { app.registeredAClock("popo", "every.1.seconds") }
        }
        threads.forEach { it.start() }
        threads.forEach { it.join() }
    }

    @Test
    fun retrieveNotFoundOnNonExistingClock() {
        app.fetchUnknownClock()
        app.retrievedNotFoundError()
    }

    @Test
    fun stopTicksOnClockPause() {
        var clock = app.registeredAClock("to-be-disabled", "every.2.seconds")
        client.startListenTo(clock)
        clock = app.pauseClock(clock)
        client.receivesNoMoreTicks()
        assertThat(clock.status).isEqualTo("PAUSED")
        app.pauseActionIsNotAvailableFor(clock)
    }

    @Test
    fun failOnUnknownClockAction() {
        val clock = app.registeredAClock("stam", "every.1.seconds")
        app.invokeUnknownActionOn(clock)
        app.retrievedNotFoundError()
    }

    @Test
    fun retrieveAllClocksByName() {
        app.registeredAClock("hop", "every.1.minutes")
        val clock = app.registeredAClock("lala", "every.1.minutes")
        app.clocks(mapOf("name" to "lala"), containsOnly(clock))
    }

    @Test
    fun sendTickToAnExistingClock() {
        val clock = app.registeredAClock("disabled", "@never")
        client.startListenTo(clock)
        app.tick(clock)
        client.receivedTicksFor(clock)
    }
}


