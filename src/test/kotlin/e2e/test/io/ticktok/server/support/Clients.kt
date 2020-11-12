package e2e.test.io.ticktok.server.support

import com.google.gson.Gson
import com.google.gson.JsonArray
import org.apache.http.client.fluent.Request
import org.apache.http.util.EntityUtils
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import java.lang.Thread.sleep


interface Client {
    fun addClock(clock: Clock)
    fun receivesNoMoreTicks(): Boolean
    fun receivedTicksFor(clock: Clock)

}

class AnyClient : Client {
    private var client:Client? = null

    override fun addClock(clock: Clock) {
        if(client == null) {
            client = createClientFor(clock)
        }
        client?.addClock(clock)
    }

    private fun createClientFor(clock: Clock): Client {
        return when (clock.channel!!.type) {
            "rabbit" -> RabitClient()
            "http" -> HttpClient()
            else -> throw NotImplementedError("${clock.channel!!.type} isn't supported")
        }
    }

    override fun receivesNoMoreTicks() {
        client?.receivesNoMoreTicks()
    }

    override fun receivedTicksFor(clock: Clock) {
        client?.receivedTicksFor(clock)
    }
}

class HttpClient : Client {
    private val urls = hashMapOf<String, String>()

    override fun addClock(clock: Clock) {
        urls[clock.id] = clock.channel!!.details["url"]!!
    }

    override fun receivesNoMoreTicks(): Boolean {
        sleep(1000)
        urls.values.forEach { url ->
            val response = Request.Get(url).execute().returnResponse()
            val content = EntityUtils.toString(response.entity)
            val ticks = Gson().fromJson(content, JsonArray::class.java).asJsonArray
            if(ticks.size() > 0)
                return false
        }
        return true
    }

    override fun receivedTicksFor(clock: Clock) {
        urls.values.forEach { url ->
            val response = Request.Get(url).execute().returnResponse()
            val content = EntityUtils.toString(response.entity)
            val ticks = Gson().fromJson(content, JsonArray::class.java).asJsonArray
            if(ticks.size() > 0)
                return false
        }
    }
}