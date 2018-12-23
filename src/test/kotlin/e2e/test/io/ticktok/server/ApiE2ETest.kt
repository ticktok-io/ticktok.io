package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.support.App
import e2e.test.io.ticktok.server.support.App.ClockMatcher.Companion.containsClock
import e2e.test.io.ticktok.server.support.TickListener
import e2e.test.io.ticktok.server.support.TickListener.Companion.CLOCK_EXPR
import org.hamcrest.Matchers.not
import org.junit.FixMethodOrder
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.runners.MethodSorters
import java.lang.Thread.sleep

@TestInstance(PER_CLASS)
class ApiE2ETest : AppE2ETest() {

    private val client = TickListener()

    @Test
    fun registerNewClock() {
        App.registeredAClock("kuku", CLOCK_EXPR)
        App.retrievedRegisteredClock(CLOCK_EXPR)
    }

    @Test
    fun retrieveScheduledMessage() {
        val clock = App.registeredAClock("kuku", CLOCK_EXPR)
        client.receivedTicksFor(clock)
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

        App.clocks(containsClock(clock1))
        App.clocks(containsClock(clock2))
    }

    @Test
    fun purgeClocks() {
        val clock = App.registeredAClock("purger", CLOCK_EXPR)
        client.receivedTicksFor(clock)
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
            Thread { App.registeredAClock("kuku", "every.1.seconds") }
        }
        threads.forEach { it.start() }
        threads.forEach { it.join() }
    }

}


