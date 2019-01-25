package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.support.App
import e2e.test.io.ticktok.server.support.App.ClockMatcher.Companion.containsClock
import e2e.test.io.ticktok.server.support.AppE2ETest
import e2e.test.io.ticktok.server.support.Client
import e2e.test.io.ticktok.server.support.Client.CLOCK_EXPR
import e2e.test.io.ticktok.server.support.Clock
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import java.lang.Thread.sleep

@TestInstance(PER_CLASS)
class ApiE2ETest : AppE2ETest() {


    @Test
    fun registerNewClock() {
        App.registeredAClock("kuku", CLOCK_EXPR)
        App.retrievedRegisteredClock("kuku", CLOCK_EXPR)
    }

    @Test
    @Tag("sanity")
    fun retrieveScheduledMessage() {
        val clock = App.registeredAClock("kuku", CLOCK_EXPR)
        Client.receivedTicksFor(clock)
    }

    @Test
    fun shouldBeHealthy() {
        App.isHealthy()
    }

    @Test
    fun failWhenTokenNotProvided() {
        App.isAccessedWithoutAToken()
        App.retrieveAuthError()
    }

    @Test
    fun retrieveConfiguredClocks() {
        val clock1 = App.registeredAClock("kuku6", "every.6.seconds")
        val clock2 = App.registeredAClock("popo10", "every.10.seconds")

        App.clocks(containsClock(clock1.copy(status = Clock.ACTIVE)))
        App.clocks(containsClock(clock2.copy(status = Clock.ACTIVE)))
    }

    @Test
    fun purgeClocks() {
        val clock = App.registeredAClock("purger", CLOCK_EXPR)
        Client.reset()
        App.purge()
        App.clocks(not(containsClock(clock)))
    }

    @Test
    fun failOnInValidSchedule() {
        App.registeredAClock("kuku", "non-valid")
        App.retrievedUserError()
    }

    @RepeatedTest(value = 2, name = "handleConcurrentClockRequests {currentRepetition}/{totalRepetitions}")
    fun handleConcurrentClockRequests() {
        App.purge()
        sleep(500)
        invokeMultipleClockRequestsInParallel()
        App.allInteractionsSucceeded()
    }

    private fun invokeMultipleClockRequestsInParallel() {
        val threads = (0..5).map {
            Thread { App.registeredAClock("popo", "every.1.seconds") }
        }
        threads.forEach { it.start() }
        threads.forEach { it.join() }
    }

    @Test
    fun retrieveNotFoundOnNonExistingClock() {
        App.fetchUnknownClock()
        App.retrievedNotFoundError()
    }

    @Test
    fun stopTicksOnClockPause() {
        val clock = App.registeredAClock("to-be-disabled", "every.2.seconds")
        App.pauseClock(clock)
        Client.receivesNoMoreTicks()
    }

    @Test
    fun failOnUnknownClockAction() {
        val clock = App.registeredAClock("stam", "every.1.seconds")
        App.invokeUnknownActionOn(clock)
        App.retrievedNotFoundError()
    }
}


