package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.support.App
import e2e.test.io.ticktok.server.support.Client
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("ct")
abstract class CommonAppE2ETest {

    @AfterEach
    open fun resetApp() {
        app().reset()
        client().stop()
    }

    abstract fun app(): App

    abstract fun client(): Client

    @AfterAll
    open fun purgeApp() {
        app().purge()
        app().shutdown()
    }

    @Test
    fun retrieveScheduledTicks() {
        // Given
        val clock = app().registeredAClock("kuku", Client.CLOCK_EXPR)
        client().addClock(clock)
        // Expect
        client().receivedTicksFor(clock)
    }
}