package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.support.*
import e2e.test.io.ticktok.server.support.App.Companion.NULL
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("ct")
abstract class CommonAppE2ETest {

    var client: Client = Client()
    var app: AppDriver = App.instance(NULL)

    @BeforeEach
    open fun startClient() {
        client = Client(tickConsumer())
        app = App.instance(appProfile())
    }

    abstract fun appProfile(): String

    abstract fun tickConsumer(): TickConsumer

    @AfterEach
    open fun resetApp() {
        app.reset()
        client.stop()
    }

    @AfterAll
    open fun purgeApp() {
        app.purge()
        app.shutdown()
    }

    @Test
    fun retrieveScheduledTicks() {
        // Given
        val clock = app.registeredAClock("kuku", Client.CLOCK_EXPR)
        client.startListenTo(clock)
        // Expect
        client.receivedTicksFor(clock)
    }

}