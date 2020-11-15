package e2e.io.ticktok.server

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovyx.net.http.URIBuilder
import io.ticktok.server.Application
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.client.fluent.Request
import org.apache.http.entity.ContentType
import org.hamcrest.BaseMatcher
import org.hamcrest.MatcherAssert
import org.junit.Assert

import java.net.http.HttpClient

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
        def health = Request.Get("$appUrl/mgmt/health").execute().returnContent().asStream()
        return new JsonSlurper().parse(health).status
    }

    def isHealthy() {
        isAppHealthy()
    }

    void accessedWithoutAToken() {
        lastResponses.add(Request.Post("$appUrl/api/v1/clocks")
                .bodyString(json([name: "no-token", schedule: "in.1.minute"]), ContentType.APPLICATION_JSON)
                .execute().returnResponse())
    }

    void retrieveAuthError() {
        assert lastResponses[0].statusLine.statusCode == HttpStatus.SC_FORBIDDEN
    }

    void clocks(Map<String, String> filter = [:], matcher) {
        MatcherAssert.assertThat(getAllClocks(filter), matcher)
    }

    private def getAllClocks(Map<String, String> filter = [:]) {
        def response = Request.Get(createAuthenticatedUrlFor("/api/v1/clocks", filter))
                .execute().returnContent().asStream()
        return new JsonSlurper().parse(response)
    }

    void reset() {
        lastResponses.clear()
    }

    void purge() {
        resumeAllPausedClocks()
        sleep(1500)
    }

    private def resumeAllPausedClocks() {
        getAllClocks().each { c ->
            Request.Put(createAuthenticatedUrlFor("/api/v1/clocks/${c.id}/resume")).execute()
        }
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
        use(MapOps) {
            assert isSameClock(getClockFrom(url), lastResponseBody)
        }
    }

    private boolean isSameClock(clock1, clock2) {
        (clock1 - 'status' - 'channel') == (clock2 - 'status' - 'channel')
    }

    private def getClockFrom(url) {
        new JsonSlurper().parse(Request.Get(url).execute().returnContent().asStream())
    }

    private String lastResponseLocation() {
        String location = lastResponses[0].getFirstHeader("Location").value
        assert location?.trim(), "Location header is empty"
        return location
    }

    String json(Map map) {
        new JsonBuilder(map).toString()
    }

    static class MapOps {
        static def minus(Map map, key) {
            map.findAll { it.key != key }
        }
    }

    public static class ClockMatcher extends BaseMatcher<List> {

        def clock
        def exclusive

        ClockMatcher(clock, exclusive = false) {
            this.clock = clock
            this.exclusive = exclusive
        }

        def describeTo(description) {
            description?.appendText(clock.toString())
        }

        boolean matches(clocks) {
            def clockCount = clocks.findAll { it -> isSameClock(it, this.clock)}.size()
            return (!exclusive && clockCount > 0) || (clockCount == 1 && item.size == 1)
        }

        static ClockMatcher containsClock(clock : Clock) {
            return ClockMatcher(clock)
        }

        static ClockMatcher containsOnly(clock : Clock) {
            return ClockMatcher(clock, true)
        }
    }
}

}
