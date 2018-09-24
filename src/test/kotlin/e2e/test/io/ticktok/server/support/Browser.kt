package e2e.test.io.ticktok.server.support

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import java.net.URI
import java.util.concurrent.TimeUnit

class Browser {

    lateinit var driver: WebDriver

    fun start() {
        driver = ChromeDriver()
        driver.get(URI("http://localhost:8090/").toString())
        driver.manage()?.timeouts()?.implicitlyWait(10, TimeUnit.SECONDS)
    }

    fun stop() {
        driver.close()
    }
}