package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.pages.ClockListPage
import e2e.test.io.ticktok.server.support.App
import e2e.test.io.ticktok.server.pages.Browser
import org.junit.jupiter.api.*
import org.junit.jupiter.api.condition.DisabledIfSystemProperty

@DisabledIfSystemProperty(named = "scope", matches = "server")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DashboardE2ETest {

    private val browser = Browser()

    @BeforeAll
    fun setup() {
        App.start()
        browser.start()
    }

    @Test
    fun showConfiguredClocks() {
        App.registeredAClock("every.9.seconds")
        App.registeredAClock("every.4.seconds")
        ClockListPage(browser).containsClockWith("every.9.seconds")
        ClockListPage(browser).containsClockWith("every.4.seconds")
    }

    @AfterEach
    fun resetApp() {
        App.reset()
    }

    @AfterAll
    fun stopBrowser() {
        browser.stop()
    }

}