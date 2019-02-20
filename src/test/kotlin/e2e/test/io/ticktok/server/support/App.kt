package e2e.test.io.ticktok.server.support

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.rabbitmq.client.AMQP
import io.ticktok.server.Application
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.client.fluent.Request
import org.apache.http.entity.ContentType
import org.apache.http.util.EntityUtils
import org.awaitility.Duration
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.awaitility.kotlin.withPollInterval
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.json.JSONObject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import java.lang.Thread.sleep
import java.util.*

class App {

    companion object {
        const val ACCESS_TOKEN = "ct-auth-token"
        var appUrl = System.getenv("APP_URL") ?: "http://localhost:8080"
        private var appInstance: App? = null

        fun instance(profile: String): App {
            if (appInstance == null) {
                appInstance = App()
                if (System.getProperty("startApp", "yes") != "no") {
                    appInstance?.start(profile)
                }
            }
            appInstance?.updateActiveProfileTo(profile)
            return appInstance as App;
        }
    }

    private val lastResponses: MutableList<HttpResponse> = Collections.synchronizedList(ArrayList())
    private var currentProfile: String = ""

    fun start(profile: String) {
        Application.main()
        waitForAppToBeHealthy()
    }

    fun updateActiveProfileTo(profile: String) {
        if(currentProfile != profile) {
            currentProfile = profile
            val response = Request.Get(createAuthenticatedUrlFor("/admin/restart?profiles=$profile")).execute().returnResponse()
            assertThat(response.statusLine.statusCode, `is`(200))
            waitForAppToBeHealthy()
            println("App is healthy!")
        }
    }

    private fun waitForAppToBeHealthy() {
        println("Waiting for app($appUrl) to be healthy...")
        await withPollInterval (Duration.ONE_SECOND) atMost (Duration.FIVE_MINUTES) until { isAppHealthy() }
    }

    fun reset() {
        lastResponses.clear()
    }

    fun registeredAClock(name: String, timeExpr: String): Clock {
        val response = requestClock(name, timeExpr)
        lastResponses.add(response)
        val clock = Gson().fromJson<Clock>(bodyOf(response))
        startListenOn(clock)
        return clock
    }

    private fun requestClock(name: String, timeExpr: String): HttpResponse {
        return Request.Post(createAuthenticatedUrlFor("/api/v1/clocks"))
                .bodyString(createClockRequestFor(name, timeExpr), ContentType.APPLICATION_JSON)
                .execute().returnResponse()
    }

    private fun bodyOf(response: HttpResponse) = EntityUtils.toString(response.entity)

    private fun createAuthenticatedUrlFor(slag: String): String {
        return withAuthToken("$appUrl/$slag")
    }

    private fun withAuthToken(url: String?): String {
        return "$url${if (url!!.indexOf("?") > -1) "&" else "?"}access_token=$ACCESS_TOKEN"
    }

    private fun createClockRequestFor(name: String, timeExpr: String): String {
        return JSONObject()
                .put("schedule", timeExpr)
                .put("name", name)
                .toString()
    }

    private inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object : TypeToken<T>() {}.type)!!

    private fun startListenOn(clock: Clock) {
        if (clock.channel != null) {
            validateTickChannelType(clock)
            Client.startListenTo(clock)
        }
    }

    private fun validateTickChannelType(clock: Clock) {
        assertThat(clock.channel!!.type, `is`(currentProfile))
    }

    fun isHealthy() {
        assertTrue(isAppHealthy())
    }

    private fun isAppHealthy(): Boolean {
        return try {
            getHealthStatus() == "UP"
        } catch (t: Throwable) {
            println("Failed to fetch health: ${t.message}")
            false
        }
    }

    private fun getHealthStatus(): String {
        val health = Request.Get("$appUrl/mgmt/health").execute().returnContent().asString()
        return Gson().fromJson(health, JsonObject::class.java).get("status").asString
    }

    fun isAccessedWithoutAToken() {
        lastResponses.add(Request.Post("$appUrl/api/v1/clocks")
                .bodyString(createClockRequestFor("no-token", "in.1.minute"), ContentType.APPLICATION_JSON)
                .execute().returnResponse())
    }

    fun retrieveAuthError() {
        assertThat(lastResponses[0].statusLine.statusCode, `is`(HttpStatus.SC_FORBIDDEN))
    }

    fun clocks(matcher: Matcher<List<Clock>>) {
        assertThat(getAllClocks(), matcher)
    }

    private fun getAllClocks(): List<Clock> {
        val response = Request.Get(createAuthenticatedUrlFor("/api/v1/clocks")).execute().returnContent().asString()
        return Gson().fromJson(response, Array<Clock>::class.java).asList()
    }

    fun retrievedRegisteredClock(name: String, clockExpr: String) {
        assertThat(lastResponses[0].statusLine.statusCode, `is`(HttpStatus.SC_CREATED))
        validateRetrievedBody(name, clockExpr)
        validateRetrievedLocation()
    }

    private fun validateRetrievedLocation() {
        val lastResponseBody = lastResponseBody()
        val url = lastResponseLocation()
        assertThat(getAsClock(url), `is`(toClock(lastResponseBody).copy(status = Clock.ACTIVE)))
    }

    private fun lastResponseBody(): JsonObject {
        val respBody = EntityUtils.toString(lastResponses[0].entity)
        return Gson().fromJson(respBody, JsonObject::class.java)
    }

    private fun validateRetrievedBody(name: String, clockExpr: String) {
        val lastResponseBody = lastResponseBody()
        assertThat(lastResponseBody.get("name").asString, `is`(name))
        assertThat(lastResponseBody.get("schedule").asString, `is`(clockExpr))
    }

    private fun lastResponseLocation(): String {
        val location = lastResponses[0].getFirstHeader("Location").value
        Assertions.assertFalse(location.isNullOrEmpty(), "Location header is empty")
        return location
    }

    private fun getAsClock(url: String): Clock {
        return Gson().fromJson<Clock>(
                Request.Get(url).execute().returnContent().asString(),
                Clock::class.java)
    }

    private fun toClock(clockJson: JsonObject): Clock {
        return Gson().fromJson(clockJson, Clock::class.java)
    }

    fun retrievedUserError() {
        assertThat(lastResponses.last().statusLine.statusCode, `is`(HttpStatus.SC_BAD_REQUEST))
    }

    fun purge() {
        resumeAllPausedClocks()
        sleep(500)
        val response = Request.Post(createAuthenticatedUrlFor("/api/v1/clocks/purge")).execute().returnResponse()
        sleep(500)
        assertThat(response.statusLine.statusCode, `is`(204))
    }

    private fun resumeAllPausedClocks() {
        getAllClocks().forEach { c ->
            Request.Put(createAuthenticatedUrlFor("/api/v1/clocks/${c.id}/resume")).execute()
        }
    }

    fun allInteractionsSucceeded() {
        val failedRequestsCount = lastResponses
                .map { r -> r.statusLine.statusCode }
                .filter { sc -> sc !in 200..299 }
                .count()
        assertThat(failedRequestsCount, `is`(0))
    }

    fun pauseClock(clock: Clock) {
        val response = Request.Put(createAuthenticatedUrlFor("/api/v1/clocks/${clock.id}/pause")).execute().returnResponse()
        assertThat(response.statusLine.statusCode, `is`(204));
    }

    fun clock(id: String): Clock {
        val result = Request.Get(createAuthenticatedUrlFor("/api/v1/clocks/${id}")).execute().returnContent().asString()
        return Gson().fromJson(result, Clock::class.java)
    }

    fun fetchUnknownClock() {
        val response = Request.Get(createAuthenticatedUrlFor("/api/v1/clocks/unknown-id")).execute().returnResponse()
        lastResponses.add(response)
    }

    fun retrievedNotFoundError() {
        assertThat(lastResponses.last().statusLine.statusCode, `is`(404))
    }

    fun invokeUnknownActionOn(clock: Clock) {
        val response = Request.Put(createAuthenticatedUrlFor("/api/v1/clocks/${clock.id}/unknown")).execute().returnResponse()
        lastResponses.add(response)
    }

    class ClockMatcher(private val clock: Clock) : BaseMatcher<List<Clock>>() {
        override fun describeTo(description: Description?) {
            description?.appendText(clock.toString())
        }

        override fun matches(item: Any?): Boolean {
            return (item as List<*>).firstOrNull {
                it == clock.copy(status = (it as Clock).status)
            } != null
        }

        companion object {
            fun containsClock(clock: Clock): ClockMatcher {
                return ClockMatcher(clock)
            }

        }

    }

}
