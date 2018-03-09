package e2e.test.io.ticktok.server.support

import com.google.gson.Gson
import com.google.gson.JsonObject
import io.ticktok.broadcast.Application
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.client.fluent.Request
import org.apache.http.entity.ContentType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.json.JSONObject

class AppDriver {
    companion object {
        const val APP_URL = "http://localhost:8080"
        const val CLIENT_ID = "e2e-client"
        const val ACCESS_TOKEN = "1234"
    }

    private var lastResponse: HttpResponse? = null

    fun start() {
        Application.main()
    }

    fun startClocking(timeExpr: String) {
        val response = Request.Post(createAuthenticatedUrlFor("/api/v1/clocks"))
                .bodyString(createClockRequestFor(timeExpr), ContentType.APPLICATION_JSON)
                .execute().returnResponse()
        assertThat(response.statusLine.statusCode, `is`(HttpStatus.SC_CREATED))
    }

    private fun createAuthenticatedUrlFor(slag: String): String {
        return String.format("%s/%s?access_token=%s", APP_URL, slag, ACCESS_TOKEN)
    }

    private fun createClockRequestFor(timeExpr: String): String {
        return JSONObject()
                .put("schedule", timeExpr)
                .put("clientId", CLIENT_ID)
                .toString()
    }

    fun isHealthy() {
        assertThat(getHealthStatus(), `is`("UP"))
    }

    private fun getHealthStatus(): String {
        val health = Request.Get("http://localhost:8081/health").execute().returnContent().asString()
        return Gson().fromJson(health, JsonObject::class.java).get("status").asString
    }

    fun isAccessedWithoutAToken() {
        lastResponse = Request.Post("$APP_URL/api/v1/clocks")
                .bodyString(createClockRequestFor("in.1.minute"), ContentType.APPLICATION_JSON)
                .execute().returnResponse()
    }

    fun retrieveAuthError() {
        assertThat(lastResponse!!.statusLine.statusCode, `is`(403))
    }
}