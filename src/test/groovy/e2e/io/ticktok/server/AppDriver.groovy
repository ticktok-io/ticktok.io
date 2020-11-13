package e2e.io.ticktok.server

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovyx.net.http.AsyncHTTPBuilder
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.URIBuilder
import io.ticktok.server.Application
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.client.fluent.Request
import org.apache.http.entity.ContentType
import org.apache.http.message.BasicNameValuePair
import org.assertj.core.api.Assertions

import java.net.http.HttpClient

import static java.time.Duration.ofMinutes
import static java.time.Duration.ofSeconds
import static org.assertj.core.api.Assertions.assertThat
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

    private List<HttpResponse> lastResponses = []
    private String currentProfile
    def http = HttpClient.newHttpClient()

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
            return getHealthStatus() == "UP"
        } catch (Throwable t) {
            println("Failed to fetch health: ${t.message}")
            return false
        }
    }

    private String getHealthStatus() {
        println("Before")
        def health = Request.Get("$appUrl/mgmt/health").execute().returnContent().asStream()
        println("After")
        return new JsonSlurper().parse(health).status
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
            assert Request.Post("$appUrl/mgmt/shutdown").execute().returnResponse().statusLine.statusCode == 200
        }
        appInstance = null
        currentProfile = ""
    }

    def registeredAClock(String name, String schedule) {
        return postClock(name, schedule)

    }

    private def postClock(name, schedule) {
        def response = Request.Post(createAuthenticatedUrlFor("/api/v1/clocks"))
                .bodyString(new JsonBuilder([name: name, schedule: schedule]).toString(), ContentType.APPLICATION_JSON)
                .execute().returnResponse()
        lastResponses.add(response)
        return bodyOf(response)
    }

    private Object bodyOf(HttpResponse response) {
        new JsonSlurper().parse(response.getEntity().getContent())
    }

    private def createAuthenticatedUrlFor(slag, params = [:]) {
        return new URIBuilder(appUrl)
                .setPath(slag)
                .setQuery(params + [access_token: ACCESS_TOKEN]).toString()
    }

    void retrievedRegisteredClock(name, schedule) {
        assert lastResponses[0].statusLine.statusCode == HttpStatus.SC_CREATED
        validateRetrievedBody(name, schedule)
        validateRetrievedLocation()
    }

    void validateRetrievedBody(String name, String schedule) {
        assert bodyOf(lastResponses[0]).name == name
        assert bodyOf(lastResponses[0]).schedule == schedule
    }

    private void validateRetrievedLocation() {
        def lastResponseBody = bodyOf(lastResponses[0])
        def url = lastResponseLocation()
        assertThat(getAsClock(url)).isEqualTo(lastResponseBody, "channel", "status")
    }

    private def getAsClock(url) {
        new JsonSlurper().parse(Request.Get(url).execute().returnContent().asStream())
    }

    private String lastResponseLocation() {
        String location = lastResponses[0].getFirstHeader("Location").value
        assert location?.trim(), "Location header is empty"
        return location
    }
}
