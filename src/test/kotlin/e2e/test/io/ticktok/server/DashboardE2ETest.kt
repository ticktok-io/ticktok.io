package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.pages.Browser
import e2e.test.io.ticktok.server.pages.ClockListPage
import e2e.test.io.ticktok.server.support.App
import e2e.test.io.ticktok.server.support.AppE2ETest
import org.junit.Before
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.condition.DisabledIfSystemProperty
import java.lang.Thread.sleep

@DisabledIfSystemProperty(named = "scope", matches = "server")
@TestInstance(PER_CLASS)
class DashboardE2ETest : AppE2ETest() {

    private val browser = Browser()
    private var clockListPage = ClockListPage(browser)

    @BeforeAll
    override fun startApp() {
        super.startApp()
        browser.start()

    }

    @Test
    fun showConfiguredClocks() {
        App.registeredAClock("row1", "every.911.seconds")
        App.registeredAClock("row2", "every.888.seconds")
        clockListPage.clockNamed("row1").contains("every.911.seconds")
        clockListPage.clockNamed("row2").contains("every.888.seconds")
    }

    @Test
    fun disableAClock() {
        App.registeredAClock("kuku", "every.911.seconds")
        val clockListPage = ClockListPage(browser)
        clockListPage.clockNamed("kuku").clickAction()
        clockListPage.clockNamed("kuku").actionIs("Resume")
    }

    @AfterAll
    fun stopBrowser() {
        browser.stop()
    }

}