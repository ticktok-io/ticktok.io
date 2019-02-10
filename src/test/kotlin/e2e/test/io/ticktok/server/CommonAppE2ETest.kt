package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.support.App
import e2e.test.io.ticktok.server.support.Client
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class CommonAppE2ETest {

    @AfterEach
    open fun resetApp() {
        app().reset()
        Client.stop()
    }

    abstract fun app(): App

    @AfterAll
    open fun purgeApp() {
        app().purge()
    }

    @Test
    fun retrieveScheduledMessage() {
        val clock = app().registeredAClock("kuku", Client.CLOCK_EXPR)
        Client.receivedTicksFor(clock)
    }

}