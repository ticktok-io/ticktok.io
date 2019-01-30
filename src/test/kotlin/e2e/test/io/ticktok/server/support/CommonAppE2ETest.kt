package e2e.test.io.ticktok.server.support

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import java.lang.Thread.sleep

abstract class CommonAppE2ETest {

    @AfterEach
    open fun resetApp() {
        app().reset()
        Client.stop()
    }

    @AfterAll
    open fun purgeApp() {
        app().purge()
    }


    fun app(): App {
        return App.instance()
    }

}