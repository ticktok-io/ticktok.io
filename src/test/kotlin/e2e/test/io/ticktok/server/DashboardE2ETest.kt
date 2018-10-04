package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.pages.ClockListPage
import e2e.test.io.ticktok.server.support.App
import e2e.test.io.ticktok.server.support.Browser
import org.junit.Ignore
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DashboardE2ETest {

    private val browser = Browser()

    @BeforeEach
    fun startBrowser() {
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
        browser.stop()
    }

}