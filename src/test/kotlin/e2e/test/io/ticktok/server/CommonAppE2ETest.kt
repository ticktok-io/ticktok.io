package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.support.*
import org.junit.jupiter.api.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("ct")
abstract class CommonAppE2ETest {

    companion object {
        var client: Client = Client()
    }
    var app: App = App()

    @BeforeAll
    fun startApp() {
        app.start(appProfile())
        client = Client(tickConsumer())
    }

    @BeforeEach
    open fun startClient() {
        client = Client(tickConsumer())
    }

    abstract fun appProfile(): String

    open fun tickConsumer(): TickConsumer {
        return AnyTickConsumer()
    }

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
}