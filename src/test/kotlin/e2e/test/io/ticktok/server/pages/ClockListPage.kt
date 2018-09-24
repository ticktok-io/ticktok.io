package e2e.test.io.ticktok.server.pages

import e2e.test.io.ticktok.server.support.Browser
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.PageFactory

class ClockListPage(private val browser: Browser) {

    @FindBy(id = "greeting")
    private val greeting: WebElement? = null

    init {
        PageFactory.initElements(browser.driver, this)
    }

    fun verifyGreetingIs(greeting: String) {
        assertThat(this.greeting?.text, `is`(greeting))
    }

    fun containsRowFor(schedule: String) {

    }


}