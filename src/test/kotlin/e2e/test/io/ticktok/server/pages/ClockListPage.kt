package e2e.test.io.ticktok.server.pages

import org.awaitility.Duration
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
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

        fun actionIs(name: String) {
            await atMost Duration.ONE_SECOND until { row!!.findElement(By.tagName("button")).text == name }
        }
    }
}