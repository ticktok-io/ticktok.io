package e2e.test.io.ticktok.server.support

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import io.ticktok.server.Application
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.client.fluent.Request
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.ContentType
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.awaitility.Duration
import org.awaitility.kotlin.*
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsNull
import org.json.JSONObject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import java.lang.Thread.sleep
import java.util.*

class App(profile: String) {

    companion object {
        const val ACCESS_TOKEN = "ct-auth-token"
        var appUrl = System.getenv("APP_URL") ?: "http://localhost:9643"
        var startApp = System.getProperty("startApp", "yes") != "no"
        private var appInstance: App? = null

        fun instance(profile: String): App {
            if (appInstance == null) {
                appInstance = App(profile)
                if (startApp) {
                    appInstance?.start()
                }
                appInstance?.waitForAppToBeHealthy()
            }
            return appInstance as App
        }
    }

    private val lastResponses: MutableList<HttpResponse> = Collections.synchronizedList(ArrayList())
    private var currentProfile: String = profile

    fun start() {
        Application.main("--spring.profiles.active=$currentProfile")
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

    private fun createAuthenticatedUrlFor(slag: String, params: Map<String, String> = mapOf()): String {
        return URIBuilder(appUrl)
                .setPath(slag)
                .setParameters(params.map { BasicNameValuePair(it.key, it.value) })
                .setParameter("access_token", ACCESS_TOKEN).build().toString()
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
        await atMost (Duration.FIVE_SECONDS) untilAsserted {
            assertThat(getAllClocks(), matcher)
        }
    }

    fun clocks(filter: Map<String, String>, matcher: Matcher<List<Clock>>) {
        assertThat(getAllClocks(filter), matcher)
    }

    private fun getAllClocks(filter: Map<String, String> = mapOf()): List<Clock> {
        val response = Request.Get(createAuthenticatedUrlFor("/api/v1/clocks", filter))
                .execute().returnContent().asString()
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
        sleep(1500)
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

    fun pauseClock(clock: Clock) : Clock {
        return dispatchActionOn("pause", clock)
    }

    private fun dispatchActionOn(action: String, clock: Clock) : Clock {
        val pauseUrl = clock.linkFor(action)
        val response = Request.Put(withAccessToken((pauseUrl as String?)!!)).execute().returnResponse()
        assertThat(response.statusLine.statusCode, `is`(200))
        return Gson().fromJson<Clock>(bodyOf(response))
    }

    private fun withAccessToken(url: String): String {
        return URIBuilder(url)
                .setParameter("access_token", ACCESS_TOKEN).build().toString()
    }

    fun clock(id: String): Clock {
        val result = Request.Get(createAuthenticatedUrlFor("/api/v1/clocks/$id")).execute().returnContent().asString()
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

    fun shutdown() {
        if (startApp) {
            assertThat(Request.Post("$appUrl/mgmt/shutdown").execute().returnResponse().statusLine.statusCode, `is`(200))
        }
        appInstance = null
        currentProfile = ""
    }

    fun pauseActionIsNotAvailableFor(clock: Clock) {
        val updatedClock = clock(clock.id)
        assertThat(updatedClock.linkFor("pause"), IsNull())
    }

    fun tick(clock: Clock) {
        dispatchActionOn("tick", clock)
    }

    class ClockMatcher(private val clock: Clock, private val exclusive: Boolean = false) : BaseMatcher<List<Clock>>() {
        override fun describeTo(description: Description?) {
            description?.appendText(clock.toString())
        }

        override fun matches(item: Any?): Boolean {
            val clockCount = (item as List<*>).groupBy { clock.copy(status = (it as Clock).status) }.count()
            return (!exclusive && clockCount > 0) || (clockCount == 1 && item.size == 1)
        }

        companion object {
            fun containsClock(clock: Clock): ClockMatcher {
                return ClockMatcher(clock)
            }

            fun containsOnly(clock: Clock): ClockMatcher {
                return ClockMatcher(clock, true)


            }
        }

    }

}
