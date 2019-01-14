package e2e.test.io.ticktok.server.support

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll

abstract class AppE2ETest {

    @BeforeAll
    open fun startApp() {
        App.start()
    }

    @AfterEach
    open fun resetApp() {
        App.reset()
        Client.stop()
    }

    @AfterAll
    open fun purgeApp() {
        App.purge()
    }

}