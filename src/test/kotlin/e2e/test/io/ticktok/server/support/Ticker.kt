package e2e.test.io.ticktok.server.support

import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.http.client.fluent.Request
import org.apache.http.entity.ContentType
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.awaitility.kotlin.withAlias
import org.json.JSONObject
import java.time.Duration

class Ticker(registrationInfo: TickerRegistrationInfo) {
    companion object {
        fun create(name: String): Ticker {
            return Ticker(registerTicker(name))
        }

        private fun registerTicker(name: String): TickerRegistrationInfo {
            val response = Request.Post(createAuthenticatedUrlFor("/api/v1/tickers"))
                    .bodyString(createTickerRegisterRequest(name), ContentType.APPLICATION_JSON)
                    .execute().returnResponse()
            return Gson().fromJson(bodyOf(response))
        }

        private fun createTickerRegisterRequest(name: String): String {
            return JSONObject()
                    .put("name", name)
                    .toString()
        }
    }

    private val receivedTicks = mutableListOf<Tick>()

    init {
        val client = HttpClient {
            install(WebSockets)
        }
        GlobalScope.launch {
            client.ws(
                    registrationInfo.ws
            ) {
                while (true) {
                    val frame = incoming.receive()
                    val tick = Gson().fromJson<Tick>(frame.data.toString())
                    receivedTicks.add(tick)
                }
            }
        }
    }

    fun receivedTickFor(clock: Clock) {
        await atMost Duration.ofSeconds(2) withAlias "No ticks for: ${clock.describe()}" until {
            receivedTicks.find { it.name == clock.name && it.schedule == it.schedule } != null
        }
    }
}


data class Tick(
        val name: String,
        val schedule: String
)

