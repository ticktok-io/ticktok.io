package e2e.test.io.ticktok.server.pages

import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.PageFactory


class ClockListPage(private val browser: Browser) {

    @FindBy(className = "clock-row")
    private val clocks: List<WebElement> = listOf()

    init {
        PageFactory.initElements(browser.driver, this)
    }

    fun containsClockWith(schedule: String) {
        clocks.forEach { c ->
            if (containsScheduleOnce(c.text, schedule)) return
        }
        browser.takeScreenshot()
        fail<String>("$schedule not found")
    }

    private fun containsScheduleOnce(text: String, schedule: String): Boolean {
        val regex = """\w+\.\d+\.\w+""".toRegex()
        val matches = regex.findAll(text)
        return matches.count() == 1 && matches.first().value == schedule
    }

    fun forClock(name: String): ClockRow {
        print(clocks)
        return ClockRow(clocks.find { it.findElements(By.tagName("td"))[0].text == name })
    }

    class ClockRow(val row: WebElement?) {

        fun clickAction() {
            row!!.findElement(By.tagName("button")).click()
        }

        fun contains(s: String) {
            row!!.text.contains(s)
        }

        fun actionIs(s: String) {
            assertEquals(row!!.findElement(By.tagName("button")).text, s)
        }
    }
}