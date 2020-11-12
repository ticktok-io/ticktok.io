package e2e.io.ticktok.server

import groovyx.net.http.HTTPBuilder
import io.ticktok.server.Application
import org.awaitility.Awaitility

import java.time.Duration

import static java.time.Duration.ofMinutes
import static java.time.Duration.ofSeconds
import static org.awaitility.Awaitility.await

class AppDriver {

    static String ACCESS_TOKEN = "ct-auth-token"
    static String appUrl = System.getenv("APP_URL") ?: "http://localhost:9643"
    static boolean startApp = System.getProperty("startApp", "yes") != "no"

    private static AppDriver appInstance = null


    static AppDriver instance(profile = "") {
        if (appInstance == null) {
            appInstance = new AppDriver(profile)
            if (startApp) {
                appInstance.start()
            }
            appInstance.waitForAppToBeHealthy()
        }
        return appInstance
    }

    private String currentProfile
    private def http = new HTTPBuilder(appUrl, "application/json")

    AppDriver(String profile) {
        this.currentProfile = profile
    }

    private void start() {
        Application.main("--spring.profiles.active=$currentProfile")
    }

    private void waitForAppToBeHealthy() {
        println("Waiting for app($appUrl) to be healthy...")
        await().with().pollInterval(ofSeconds(1)).atMost(ofMinutes(5)).until { isAppHealthy() }
    }

    private def isAppHealthy() {
        try {
            getHealthStatus() == "UP"
        } catch (Throwable t) {
            println("Failed to fetch health: ${t.message}")
            false
        }
    }

    private String getHealthStatus() {
        http.get(path: "/mgmt/health") { resp, data ->
            return data.status
        }
    }

    def isHealthy() {
        assertTrue(isAppHealthy())
    }

    void reset() {

    }

    void purge() {

    }

    void shutdown() {
        if (startApp) {
            http.post(path: "/mgmt/shutdown") { resp, data ->
                assert resp.statusLine.statusCode == 200
            }
        }
        appInstance = null
        currentProfile = ""
    }

    void registeredAClock(String name, String schedule) {

    }

    def void retrievedRegisteredClock(name, schedule) {

    }
}
