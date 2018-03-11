package e2e.test.io.ticktok.server;

import e2e.test.io.ticktok.server.support.AppDriver
import e2e.test.io.ticktok.server.support.ClockClient
import io.ticktok.server.ClocksController.CLOCK_EXPR
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

@TestInstance(PER_CLASS)
class ApplicationE2ETest2 {

    private val app = AppDriver()
    private val client = ClockClient();

    @BeforeAll
    fun setUp() {
        app.start()
    }

    @Test
    fun sendScheduledMessage() {
        app.startClocking(CLOCK_EXPR)
        client.receivedTheClock(CLOCK_EXPR)
    }

    @Test
    fun shouldBeHealthy() {
        app.isHealthy()
    }

    @Test
    fun failWhenTokenNotProvided() {
        app.isAccessedWithoutAToken()
        app.retrieveAuthError()
    }

    @AfterEach
    fun stopClient() {
        client.stop()
    }

}


