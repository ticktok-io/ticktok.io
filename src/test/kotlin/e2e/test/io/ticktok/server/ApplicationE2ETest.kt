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
class ApplicationE2ETest {

    private val app = AppDriver()
    private val client = ClockClient();

    @BeforeAll
    fun setUp() {
        app.start()
    }

    @Test
    fun sendScheduledMessage() {
        app.registeredAClock(CLOCK_EXPR)
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

    @Test
    fun retrieveConfiguredClocks() {
        app.registeredAClock("every.6.seconds");
        app.registeredAClock("every.1.minute");

        app.clocks().contains("every.6.seconds")
        app.clocks().contains("every.1.minute")
    }

    @AfterEach
    fun stopClient() {
        client.stop()
    }

}


