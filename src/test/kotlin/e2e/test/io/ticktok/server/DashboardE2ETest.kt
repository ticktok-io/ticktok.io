package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.pages.Browser
import e2e.test.io.ticktok.server.pages.ClockListPage
import e2e.test.io.ticktok.server.support.App
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
        App.registeredAClock("row1", "every.9.seconds")
        App.registeredAClock("row2", "every.4.seconds")
        ClockListPage(browser).containsClockWith("every.9.seconds")
        ClockListPage(browser).containsClockWith("every.4.seconds")
    }

    @AfterAll
    fun resetApp() {
        App.reset()
    }

    @AfterAll
    fun stopBrowser() {
        browser.stop()
    }

}