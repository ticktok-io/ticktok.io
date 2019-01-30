package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.pages.Browser
import e2e.test.io.ticktok.server.pages.ClockListPage
import e2e.test.io.ticktok.server.support.App
import e2e.test.io.ticktok.server.support.CommonAppE2ETest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.condition.DisabledIfSystemProperty

@DisabledIfSystemProperty(named = "scope", matches = "server")
@TestInstance(PER_CLASS)
class DashboardE2ETest : CommonAppE2ETest() {

    private val browser = Browser()
    private var clockListPage = ClockListPage(browser)

    @BeforeAll
    fun startApp() {
        browser.start()

    }

    @Test
    fun showConfiguredClocks() {
        app().registeredAClock("row1", "every.911.seconds")
        app().registeredAClock("row2", "every.888.seconds")
        clockListPage.clockNamed("row1").contains("every.911.seconds")
        clockListPage.clockNamed("row2").contains("every.888.seconds")
    }

    @Test
    fun pauseAClock() {
        app().registeredAClock("kuku", "every.922.seconds")
        clockListPage.clockNamed("kuku").clickAction()
        clockListPage.clockNamed("kuku").actionIs("Resume")
    }

    @AfterEach
    fun takeScreenShot() {
        browser.takeScreenshot()
    }

    @AfterAll
    fun stopBrowser() {
        browser.stop()
    }

}