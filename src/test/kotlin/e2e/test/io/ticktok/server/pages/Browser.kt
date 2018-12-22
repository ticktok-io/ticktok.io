package e2e.test.io.ticktok.server.pages

import e2e.test.io.ticktok.server.support.App
import org.apache.http.client.fluent.Request
import org.awaitility.Duration
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import java.io.File
import java.net.URI
import java.util.concurrent.TimeUnit
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until

class Browser {

    companion object {
        val APP_URL = System.getenv("DASHBOARD_URL") ?: "http://localhost:3000"
    }

    lateinit var driver: WebDriver

    fun start() {
        await until { Request.Get(APP_URL).execute().returnResponse().statusLine.statusCode == 200 }
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