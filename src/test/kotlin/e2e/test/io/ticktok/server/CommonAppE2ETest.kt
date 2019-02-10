package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.support.App
import e2e.test.io.ticktok.server.support.Client
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach

abstract class CommonAppE2ETest {

    @AfterEach
    open fun resetApp() {
        app().reset()
        Client.stop()
    }

    abstract fun app() : App

    @AfterAll
    open fun purgeApp() {
        app().purge()
    }

}