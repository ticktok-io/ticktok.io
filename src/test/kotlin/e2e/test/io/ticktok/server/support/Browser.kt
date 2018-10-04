package e2e.test.io.ticktok.server.support

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import java.net.URI
import java.util.concurrent.TimeUnit

class Browser {

    companion object {
        val APP_URL = System.getenv("APP_URL")!!
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
}