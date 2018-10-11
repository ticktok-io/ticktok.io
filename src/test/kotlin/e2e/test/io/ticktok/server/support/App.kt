package e2e.test.io.ticktok.server.support

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import io.ticktok.server.Application
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.client.fluent.Request
import org.apache.http.entity.ContentType
import org.apache.http.util.EntityUtils
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.json.JSONObject
import org.junit.jupiter.api.Assertions

object App {

    const val APP_URL = "http://localhost:8080"
    const val CLIENT_ID = "e2e-client"
    const val ACCESS_TOKEN = "ct-auth-token"


    private var lastResponse: HttpResponse? = null
    private val createdClocks = mutableListOf<String>()
    private var started = false;

    fun start() {
        if(!started) {
            Application.main()
            started = true
        }
    }

    fun reset() {
        createdClocks.forEach {
            deleteClock(it)
        }
        createdClocks.clear()
    }

    private fun deleteClock(url: String) {
        val response = Request.Delete(withAuthToken(url)).execute().returnResponse()
        assert(response.statusLine.statusCode in 200..299) { "Failed to delete with error: ${bodyOf(response)}" }
    }

    private fun bodyOf(response: HttpResponse) = EntityUtils.toString(response.entity)

    fun registeredAClock(timeExpr: String): Clock {
        lastResponse = Request.Post(createAuthenticatedUrlFor("/api/v1/clocks"))
                .bodyString(createClockRequestFor(timeExpr), ContentType.APPLICATION_JSON)
                .execute().returnResponse()
        val clock = Gson().fromJson<Clock>(bodyOf(lastResponse!!))
        saveClockIfCreated(clock)
        return clock
    }

    private fun createAuthenticatedUrlFor(slag: String): String {
        return withAuthToken("$APP_URL/$slag")
    }

    private fun withAuthToken(url: String): String {
        return "$url?access_token=$ACCESS_TOKEN"
    }

    private fun createClockRequestFor(timeExpr: String): String {
        return JSONObject()
                .put("schedule", timeExpr)
                .put("clientId", CLIENT_ID)
                .toString()
    }

    inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object : TypeToken<T>() {}.type)!!

    private fun saveClockIfCreated(clock: Clock) {
        if (clock.url !== null) {
            createdClocks.add(clock.url)
        }
    }

    fun isHealthy() {
        assertThat(getHealthStatus(), `is`("UP"))
    }

    private fun getHealthStatus(): String {
        val health = Request.Get("$APP_URL/mgmt/health").execute().returnContent().asString()
        return Gson().fromJson(health, JsonObject::class.java).get("status").asString
    }

    fun isAccessedWithoutAToken() {
        lastResponse = Request.Post("$APP_URL/api/v1/clocks")
                .bodyString(createClockRequestFor("in.1.minute"), ContentType.APPLICATION_JSON)
                .execute().returnResponse()
    }

    fun retrieveAuthError() {
        assertThat(lastResponse!!.statusLine.statusCode, `is`(HttpStatus.SC_FORBIDDEN))
    }

    fun clocks(matcher: Matcher<List<Clock>>) {
        val response = Request.Get(createAuthenticatedUrlFor("/api/v1/clocks")).execute().returnContent().asString()
        assertThat(Gson().fromJson(response, Array<Clock>::class.java).asList(), matcher)
    }

    fun retrievedRegisteredClock(clockExpr: String) {
        assertThat(lastResponse!!.statusLine.statusCode, `is`(HttpStatus.SC_CREATED))
        validateRetrievedBody(clockExpr)
        validateRetrievedLocation()
    }

    private fun validateRetrievedLocation() {
        val lastResponseBody = lastResponseBody()
        val url = lastResponseLocation()
        assertThat(getAsClock(url), `is`(toClock(lastResponseBody)))
    }

    private fun lastResponseBody(): JsonObject {
        val respBody = EntityUtils.toString(lastResponse!!.entity)
        return Gson().fromJson(respBody, JsonObject::class.java)
    }

    private fun validateRetrievedBody(clockExpr: String) {
        val lastResponseBody = lastResponseBody()
        assertThat(lastResponseBody.get("schedule").asString, `is`(clockExpr))
        assertThat(lastResponseBody.get("url").asString, `is`(withoutToken(lastResponseLocation())))
    }

    private fun withoutToken(url: String): String {
        return url.substring(0, url.indexOf("?"))
    }

    private fun lastResponseLocation(): String {
        val location = lastResponse!!.getFirstHeader("Location").value
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

    fun deleteClock(clock: Clock) {
        assertThat(Request.Delete(withAuthToken(clock.url)).execute().returnResponse().statusLine.statusCode, `is`(HttpStatus.SC_OK))
    }

    fun retrievedUserError() {
        assertThat(lastResponse!!.statusLine.statusCode, `is`(HttpStatus.SC_BAD_REQUEST))
    }

    class ClockMatcher(private val clock: Clock) : BaseMatcher<List<Clock>>() {
        override fun describeTo(description: Description?) {

        }

        override fun matches(item: Any?): Boolean {
            return (item as List<*>).firstOrNull {
                it == clock
            } != null
        }

        companion object {
            fun containsClock(clock: Clock): ClockMatcher {
                return ClockMatcher(clock)
            }

        }

    }

}
