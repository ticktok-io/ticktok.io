package e2e.test.io.ticktok.server.pages

import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.PageFactory
import java.lang.String.format
import kotlin.test.assertTrue


class ClockListPage(private val browser: Browser) {

    private val clockRowXPath = "//tr[td//text()[contains(., '%s')]]"

    fun clockNamed(name: String): ClockRow {
        return ClockRow(browser.findElement(By.xpath(format(clockRowXPath, name))))
    }

    class ClockRow(val row: WebElement?) {

        fun clickAction() {
            row!!.findElement(By.tagName("button")).click()
        }

        fun contains(s: String) {
            assertTrue { row!!.text.contains(s) }
        }

        fun actionIs(s: String) {
            assertEquals(row!!.findElement(By.tagName("button")).text, s)
        }
    }
}