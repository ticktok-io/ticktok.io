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
import org.awaitility.Duration
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
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

object App {

    const val ACCESS_TOKEN = "ct-auth-token"
    private val APP_URL = System.getenv("APP_URL") ?: "http://localhost:8080"


    private val lastResponses: MutableList<HttpResponse> = Collections.synchronizedList(ArrayList());
    private var started = false

    fun start() {
        if (!started) {
            waitForAppToBeHealthy()
            if(System.getProperty("startApp", "no") != "no") {
                Application.main()
            }
            started = true
        }
    }

    private fun waitForAppToBeHealthy() {
        await atMost(Duration.FIVE_MINUTES) until { isAppHealthy() }
    }

    fun reset() {
        lastResponses.clear()
    }

    fun registeredAClock(name: String, timeExpr: String): Clock {
        val response = Request.Post(createAuthenticatedUrlFor("/api/v1/clocks"))
                .bodyString(createClockRequestFor(name, timeExpr), ContentType.APPLICATION_JSON)
                .execute().returnResponse()
        lastResponses.add(response)
        return Gson().fromJson(bodyOf(response))
    }

    private fun bodyOf(response: HttpResponse) = EntityUtils.toString(response.entity)

    private fun createAuthenticatedUrlFor(slag: String): String {
        return withAuthToken("$APP_URL/$slag")
    }

    private fun withAuthToken(url: String?): String {
        return "$url?access_token=$ACCESS_TOKEN"
    }

    private fun createClockRequestFor(name: String, timeExpr: String): String {
        return JSONObject()
                .put("schedule", timeExpr)
                .put("name", name)
                .toString()
    }

    private inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object : TypeToken<T>() {}.type)!!

    fun isHealthy() {
        assertTrue(isAppHealthy())
    }

    private fun isAppHealthy(): Boolean {
        return try {
            getHealthStatus() == "UP"
        } catch(t: Throwable) {
            false
        }
    }

    private fun getHealthStatus(): String {
        val health = Request.Get("$APP_URL/mgmt/health").execute().returnContent().asString()
        return Gson().fromJson(health, JsonObject::class.java).get("status").asString
    }

    fun isAccessedWithoutAToken() {
        lastResponses.add(Request.Post("$APP_URL/api/v1/clocks")
                .bodyString(createClockRequestFor("no-token", "in.1.minute"), ContentType.APPLICATION_JSON)
                .execute().returnResponse())
    }

    fun retrieveAuthError() {
        assertThat(lastResponses[0].statusLine.statusCode, `is`(HttpStatus.SC_FORBIDDEN))
    }

    fun clocks(matcher: Matcher<List<Clock>>) {
        val response = Request.Get(createAuthenticatedUrlFor("/api/v1/clocks")).execute().returnContent().asString()
        assertThat(Gson().fromJson(response, Array<Clock>::class.java).asList(), matcher)
    }

    fun retrievedRegisteredClock(clockExpr: String) {
        assertThat(lastResponses[0].statusLine.statusCode, `is`(HttpStatus.SC_CREATED))
        validateRetrievedBody(clockExpr)
        validateRetrievedLocation()
    }

    private fun validateRetrievedLocation() {
        val lastResponseBody = lastResponseBody()
        val url = lastResponseLocation()
        assertThat(getAsClock(url), `is`(toClock(lastResponseBody)))
    }

    private fun lastResponseBody(): JsonObject {
        val respBody = EntityUtils.toString(lastResponses[0].entity)
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
        assertThat(lastResponses[0].statusLine.statusCode, `is`(HttpStatus.SC_BAD_REQUEST))
    }

    fun purge() {
        sleep(500)
        val response = Request.Post(createAuthenticatedUrlFor("/api/v1/clocks/purge")).execute().returnResponse()
        sleep(500)
        assertThat(response.statusLine.statusCode, `is`(204))
    }

    fun allInteractionsSucceeded() {
        val failedRequestsCount = lastResponses
                .map { r -> r.statusLine.statusCode }
                .filter { sc -> sc !in 200..299 }
                .count()
        assertThat(failedRequestsCount, `is`(0))
    }

    class ClockMatcher(private val clock: Clock) : BaseMatcher<List<Clock>>() {
        override fun describeTo(description: Description?) {
            description?.appendText(clock.toString())
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
