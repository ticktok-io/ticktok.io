package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.support.App
import e2e.test.io.ticktok.server.support.App.ClockMatcher.Companion.containsClock
import e2e.test.io.ticktok.server.support.TickListener
import e2e.test.io.ticktok.server.support.TickListener.Companion.CLOCK_EXPR
import javafx.scene.input.TransferMode
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import java.lang.Thread.sleep
import kotlin.concurrent.thread

@TestInstance(PER_CLASS)
class ApplicationE2ETest {

    private val client = TickListener()

    @BeforeAll
    fun startApp() {
        App.start()
    }

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
        App.purgeClocks()
        App.clocks(not(containsClock(clock)))
    }

    @Test
    fun deleteAClock() {
        val clock = App.registeredAClock("kuku9", "every.9.seconds")
        App.deleteClock(clock)
        App.clocks(not(containsClock(clock)))
    }

    @Test
    fun failOnNonValidSchedule() {
        App.registeredAClock("kuku", "non-valid")
        App.retrievedUserError()
    }

    @Test
    fun handleConcurrentRegisters() {
        val t = listOf(
            Thread {
                App.registeredAClock("kuku", "every.1.seconds")
            },
            Thread {
                App.registeredAClock("kuku", "every.1.seconds")
            },
            Thread {
                App.registeredAClock("kuku", "every.1.seconds")
            },
            Thread {
                App.registeredAClock("kuku", "every.1.seconds")
            }
        )
        t.forEach { it.start() }
        t.forEach { it.join() }


    }

    @AfterEach
    fun resetApp() {
        App.reset()
        App.purgeClocks()
        sleep(1000)
    }

}


