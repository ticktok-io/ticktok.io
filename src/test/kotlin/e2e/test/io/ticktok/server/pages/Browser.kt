package e2e.test.io.ticktok.server.pages

import e2e.test.io.ticktok.server.support.App
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import java.io.File
import java.net.URI
import java.util.concurrent.TimeUnit

class Browser {

    companion object {
        val APP_URL = System.getenv("DASHBOARD_URL")!!
    }

    lateinit var driver: WebDriver

    fun start() {
        driver = ChromeDriver()
        val appUrl = URI("$APP_URL?api_key=${App.ACCESS_TOKEN}").toString()
        println("Will open browser on: $appUrl")
        driver.get(appUrl)
        driver.manage()?.timeouts()?.implicitlyWait(10, TimeUnit.SECONDS)
    }

    fun stop() {
        driver.close()
    }

    fun takeScreenshot() {
        val scrFile = (driver as TakesScreenshot).getScreenshotAs(OutputType.FILE)
        scrFile.copyTo(File("/tmp/ticktok-artifacts/ticktok-ss-${System.currentTimeMillis()}.png"))
    }
}