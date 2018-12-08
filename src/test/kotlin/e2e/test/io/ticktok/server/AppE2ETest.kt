package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.support.App
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll

abstract class AppE2ETest {

    @BeforeAll
    fun startApp() {
        App.start()
    }

    @AfterEach
    fun resetApp() {
        App.reset()
    }

    @AfterAll
    fun purgeApp() {
        App.purge()
    }

}