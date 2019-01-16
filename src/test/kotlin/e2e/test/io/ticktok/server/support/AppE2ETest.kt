package e2e.test.io.ticktok.server.support

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import java.lang.Thread.sleep

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
        sleep(2000)
        App.purge()
    }

}