package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.pages.Browser
import e2e.test.io.ticktok.server.pages.ClockListPage
import e2e.test.io.ticktok.server.support.App
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.condition.DisabledIfSystemProperty
import java.lang.Thread.sleep

@DisabledIfSystemProperty(named = "scope", matches = "server")
@TestInstance(PER_CLASS)
class DashboardE2ETest : AppE2ETest() {

    private val browser = Browser()

    @BeforeAll
    override fun startApp() {
        super.startApp()
        sleep(15000)
        browser.start()
    }

    @Test
    fun showConfiguredClocks() {
        App.registeredAClock("row1", "every.911.seconds")
        App.registeredAClock("row2", "every.888.seconds")
        ClockListPage(browser).containsClockWith("every.911.seconds")
        ClockListPage(browser).containsClockWith("every.888.seconds")
    }

    @AfterAll
    fun stopBrowser() {
        browser.stop()
    }

}