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
import org.awaitility.kotlin.*
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.hamcrest.core.IsNull
import org.json.JSONObject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import java.lang.Thread.sleep
import java.time.Duration.ofMinutes
import java.time.Duration.ofSeconds
import java.util.*


interface AppDriver {
    companion object {
        const val HTTP = "http"
        const val RABBIT = "rabbit"
        const val NULL = "null"

        var startApp = System.getProperty("startApp", "yes") != "no"
        private var appInstance: AppDriver = NullApp()

        /*fun instance(profile: String = NULL): AppDriver {
            println("Starting: $profile")
            startApp(profile)
            return appInstance
        }

        private fun shutdownApp() {
            if(startApp) {
                appInstance.shutdown()
                appInstance = NullApp()
            }
        }

        private fun startApp(profile: String) {
            appInstance = App(profile)
            if (startApp) {
                appInstance.start()
            }
            appInstance.waitForAppToBeHealthy()
        }*/
    }

    fun start(profile: String)
    fun reset()
    fun purge()
    fun shutdown()
    fun registeredAClock(name: String, schedule: String): Clock
    fun retrievedRegisteredClock(name: String, schedule: String)
    fun isHealthy()
    fun isAccessedWithoutAToken()
    fun retrieveAuthError()
    fun clocks(matcher: Matcher<List<Clock>>)
    fun clocks(filter: Map<String, String>, matcher: Matcher<List<Clock>>)
    fun allInteractionsSucceeded()
    fun fetchUnknownClock()
    fun retrievedNotFoundError()
    fun pauseClock(clock: Clock): Clock
    fun pauseActionIsNotAvailableFor(clock: Clock)
    fun invokeUnknownActionOn(clock: Clock)
    fun tick(clock: Clock)
    fun retrievedUserError()
    fun waitForAppToBeHealthy()

}

class App : AppDriver {

    companion object {
        const val ACCESS_TOKEN = "ct-auth-token"
        var appUrl = System.getenv("APP_URL") ?: "http://localhost:9643"
    }

    private val lastResponses: MutableList<HttpResponse> = Collections.synchronizedList(ArrayList())
    var startApp = System.getProperty("startApp", "yes") != "no"

    override fun start(profile: String) {
        if(startApp)
            Application.main("--spring.profiles.active=$profile")
            waitForAppToBeHealthy()
    }

    override fun waitForAppToBeHealthy() {
        println("Waiting for app($appUrl) to be healthy...")
        await withPollInterval ofSeconds(1) atMost ofMinutes(5) until { isAppHealthy() }
    }

    override fun reset() {
        lastResponses.clear()
    }

    override fun registeredAClock(name: String, schedule: String): Clock {
        val response = requestClock(name, schedule)
        lastResponses.add(response)
        return Gson().fromJson(bodyOf(response))
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

    private inline fun <reified T> Gson.fromJson(json: String) =
            this.fromJson<T>(json, object : TypeToken<T>() {}.type)!!

    override fun isHealthy() {
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

    override fun isAccessedWithoutAToken() {
        lastResponses.add(Request.Post("$appUrl/api/v1/clocks")
                .bodyString(createClockRequestFor("no-token", "in.1.minute"), ContentType.APPLICATION_JSON)
                .execute().returnResponse())
    }

    override fun retrieveAuthError() {
        assertThat(statusOf(lastResponses.first()), equalTo(HttpStatus.SC_FORBIDDEN))
    }

    override fun clocks(matcher: Matcher<List<Clock>>) {
        await atMost (ofSeconds(5)) untilAsserted {
            assertThat(getAllClocks(), matcher)
        }
    }

    override fun clocks(filter: Map<String, String>, matcher: Matcher<List<Clock>>) {
        assertThat(getAllClocks(filter), matcher)
    }

    private fun getAllClocks(filter: Map<String, String> = mapOf()): List<Clock> {
        val response = Request.Get(createAuthenticatedUrlFor("/api/v1/clocks", filter))
                .execute().returnContent().asString()
        return Gson().fromJson(response, Array<Clock>::class.java).asList()
    }

    override fun retrievedRegisteredClock(name: String, schedule: String) {
        assertThat(statusOf(lastResponses.first()), equalTo(HttpStatus.SC_CREATED))
        validateRetrievedBody(name, schedule)
        validateRetrievedLocation()
    }

    private fun statusOf(response: HttpResponse) = response.statusLine.statusCode

    private fun validateRetrievedLocation() {
        val lastResponseBody = lastResponseBody()
        val url = lastResponseLocation()
        assertThat(getAsClock(url), equalTo(toClock(lastResponseBody).copy(status = Clock.ACTIVE)))
    }

    private fun lastResponseBody(): JsonObject {
        val respBody = EntityUtils.toString(lastResponses.first().entity)
        return Gson().fromJson(respBody, JsonObject::class.java)
    }

    private fun validateRetrievedBody(name: String, clockExpr: String) {
        val lastResponseBody = lastResponseBody()
        assertThat(lastResponseBody.get("name").asString, equalTo(name))
        assertThat(lastResponseBody.get("schedule").asString, equalTo(clockExpr))
    }

    private fun lastResponseLocation(): String {
        val location = lastResponses.first().getFirstHeader("Location").value
        Assertions.assertFalse(location.isNullOrEmpty(), "Location header is empty")
        return location
    }

    private fun getAsClock(url: String): Clock {
        return Gson().fromJson(
                Request.Get(url).execute().returnContent().asString(),
                Clock::class.java)
    }

    private fun toClock(clockJson: JsonObject): Clock {
        return Gson().fromJson(clockJson, Clock::class.java)
    }

    override fun retrievedUserError() {
        assertThat(statusOf(lastResponses.last()), equalTo(HttpStatus.SC_BAD_REQUEST))
    }

    override fun purge() {
        resumeAllPausedClocks()
        sleep(1500)
    }

    private fun resumeAllPausedClocks() {
        getAllClocks().forEach { c ->
            Request.Put(createAuthenticatedUrlFor("/api/v1/clocks/${c.id}/resume")).execute()
        }
    }

    override fun allInteractionsSucceeded() {
        val failedRequestsCount = lastResponses
                .map { r -> statusOf(r) }
                .filter { sc -> sc !in 200..299 }
                .count()
        assertThat(failedRequestsCount, equalTo(0))
    }

    override fun pauseClock(clock: Clock): Clock {
        return dispatchActionOn("pause", clock)
    }

    private fun dispatchActionOn(action: String, clock: Clock): Clock {
        val pauseUrl = clock.linkFor(action)
        val response = Request.Put(withAccessToken(pauseUrl!!)).execute().returnResponse()
        assertThat(statusOf(response), equalTo(200))
        return Gson().fromJson(bodyOf(response))
    }

    private fun withAccessToken(url: String): String {
        return URIBuilder(url)
                .setParameter("access_token", ACCESS_TOKEN).build().toString()
    }

    fun clock(id: String): Clock {
        val result = Request.Get(createAuthenticatedUrlFor("/api/v1/clocks/$id")).execute().returnContent().asString()
        return Gson().fromJson(result, Clock::class.java)
    }

    override fun fetchUnknownClock() {
        val response = Request.Get(createAuthenticatedUrlFor("/api/v1/clocks/unknown-id")).execute().returnResponse()
        lastResponses.add(response)
    }

    override fun retrievedNotFoundError() {
        assertThat(statusOf(lastResponses.last()), equalTo(404))
    }

    override fun invokeUnknownActionOn(clock: Clock) {
        val response = Request.Put(createAuthenticatedUrlFor("/api/v1/clocks/${clock.id}/unknown"))
                .execute().returnResponse()
        lastResponses.add(response)
    }

    override fun shutdown() {
        if (startApp) {
            val response = Request.Post("$appUrl/mgmt/shutdown").execute().returnResponse()
            assertThat(statusOf(response), equalTo(200))
        }
    }

    override fun pauseActionIsNotAvailableFor(clock: Clock) {
        val updatedClock = clock(clock.id)
        assertThat(updatedClock.linkFor("pause"), IsNull())
    }

    override fun tick(clock: Clock) {
        dispatchActionOn("tick", clock)
    }

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


class NullApp : AppDriver {
    override fun start(profile: String) {
        TODO("Not yet implemented")
    }

    override fun reset() {
        TODO("Not yet implemented")
    }

    override fun purge() {
        TODO("Not yet implemented")
    }

    override fun shutdown() {

    }

    override fun registeredAClock(name: String, schedule: String): Clock {
        TODO("Not yet implemented")
    }

    override fun retrievedRegisteredClock(name: String, schedule: String) {
        TODO("Not yet implemented")
    }

    override fun isHealthy() {
        TODO("Not yet implemented")
    }

    override fun isAccessedWithoutAToken() {
        TODO("Not yet implemented")
    }

    override fun retrieveAuthError() {
        TODO("Not yet implemented")
    }

    override fun clocks(matcher: Matcher<List<Clock>>) {
        TODO("Not yet implemented")
    }

    override fun clocks(filter: Map<String, String>, matcher: Matcher<List<Clock>>) {
        TODO("Not yet implemented")
    }

    override fun allInteractionsSucceeded() {
        TODO("Not yet implemented")
    }

    override fun fetchUnknownClock() {
        TODO("Not yet implemented")
    }

    override fun retrievedNotFoundError() {
        TODO("Not yet implemented")
    }

    override fun pauseClock(clock: Clock): Clock {
        TODO("Not yet implemented")
    }

    override fun pauseActionIsNotAvailableFor(clock: Clock) {
        TODO("Not yet implemented")
    }

    override fun invokeUnknownActionOn(clock: Clock) {
        TODO("Not yet implemented")
    }

    override fun tick(clock: Clock) {
        TODO("Not yet implemented")
    }

    override fun retrievedUserError() {
        TODO("Not yet implemented")
    }

    override fun waitForAppToBeHealthy() {
        TODO("Not yet implemented")
    }
}