package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.pages.Browser
import e2e.test.io.ticktok.server.pages.ClockListPage
import e2e.test.io.ticktok.server.support.App
import e2e.test.io.ticktok.server.support.App.ClockMatcher.Companion.containsClock
import e2e.test.io.ticktok.server.support.Client
import e2e.test.io.ticktok.server.support.Client.CLOCK_EXPR
import e2e.test.io.ticktok.server.support.Clock
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.condition.DisabledIfSystemProperty
import java.lang.Thread.sleep

@Tag("ct")
class CoreE2ETest : CommonAppE2ETest() {

    override fun app(): App {
        return App.instance("rabbit")
    }

    @Nested
    inner class ApiTests {

        @Test
        fun registerNewClock() {
            app().registeredAClock("kuku", CLOCK_EXPR)
            app().retrievedRegisteredClock("kuku", CLOCK_EXPR)
        }

        @Test
        fun shouldBeHealthy() {
            app().isHealthy()
        }

        @Test
        fun failWhenTokenNotProvided() {
            app().isAccessedWithoutAToken()
            app().retrieveAuthError()
        }

        @Test
        fun retrieveConfiguredClocks() {
            val clock1 = app().registeredAClock("kuku6", "every.6.seconds")
            val clock2 = app().registeredAClock("popo10", "every.10.seconds")

            app().clocks(containsClock(clock1.copy(status = Clock.ACTIVE)))
            app().clocks(containsClock(clock2.copy(status = Clock.ACTIVE)))
        }

        @Test
        fun purgeClocks() {
            val clock = app().registeredAClock("purger", CLOCK_EXPR)
            Client.reset()
            app().purge()
            app().clocks(not(containsClock(clock)))
        }

        @Test
        fun failOnInValidSchedule() {
            app().registeredAClock("kuku", "non-valid")
            app().retrievedUserError()
        }

        @RepeatedTest(value = 2, name = "handleConcurrentClockRequests {currentRepetition}/{totalRepetitions}")
        fun handleConcurrentClockRequests() {
            app().purge()
            sleep(500)
            invokeMultipleClockRequestsInParallel()
            app().allInteractionsSucceeded()
        }

        private fun invokeMultipleClockRequestsInParallel() {
            val threads = (0..5).map {
                Thread { app().registeredAClock("popo", "every.1.seconds") }
            }
            threads.forEach { it.start() }
            threads.forEach { it.join() }
        }

        @Test
        fun retrieveNotFoundOnNonExistingClock() {
            app().fetchUnknownClock()
            app().retrievedNotFoundError()
        }

        @Test
        fun stopTicksOnClockPause() {
            val clock = app().registeredAClock("to-be-disabled", "every.2.seconds")
            app().pauseClock(clock)
            Client.receivesNoMoreTicks()
        }

        @Test
        fun failOnUnknownClockAction() {
            val clock = app().registeredAClock("stam", "every.1.seconds")
            app().invokeUnknownActionOn(clock)
            app().retrievedNotFoundError()
        }
    }

    @Nested
    @DisabledIfSystemProperty(named = "scope", matches = "core")
    @TestInstance(PER_CLASS)
    @Tag("ui")
    inner class DashboardTests {

        private val browser = Browser()
        private var clockListPage = ClockListPage(browser)

        @BeforeAll
        fun startBrowser() {
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

}


