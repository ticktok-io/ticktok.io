package e2e.test.io.ticktok.server.support

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.rabbitmq.client.*
import org.apache.http.client.fluent.Request
import org.apache.http.util.EntityUtils
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.hamcrest.MatcherAssert
import org.hamcrest.collection.IsMapWithSize.aMapWithSize
import java.lang.Thread.sleep
import java.nio.charset.Charset
import java.time.Duration.ofSeconds
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap


class Client(private val listener: TickConsumer = NullTickConsumer()) {

    companion object {
        const val CLOCK_EXPR = "every.2.seconds"
    }

    private val messages = ConcurrentHashMap<String, Boolean>()

    fun receivedTicksFor(clock: Clock) {
        await atMost ofSeconds(10) until {
            messages.isNotEmpty() && messages[clock.id]!!
        }
    }

    fun receivesNoMoreTicks() {
        sleep(2000)
        reset()
        sleep(2000)
        MatcherAssert.assertThat("No new messages", messages, aMapWithSize(0))
    }

    private fun reset() {
        messages.clear()
    }

    fun startListenTo(vararg clocks: Clock) {
        listener.register(arrayOf(*clocks), onTickHandler)
    }

    private val onTickHandler = { ticks: Map<String, JsonObject> ->
        for (id in ticks.keys) {
            messages[id] = true
        }
    }

    fun stop() {
        listener.stop()
    }
}

interface TickConsumer {
    fun stop()

    fun register(clocks: Array<Clock>, callback: (ticks: Map<String, JsonObject>) -> Unit)
}

class NullTickConsumer : TickConsumer {
    override fun stop() {
        throw NotImplementedError()
    }

    override fun register(clocks: Array<Clock>, callback: (ticks: Map<String, JsonObject>) -> Unit) {
        throw NotImplementedError()
    }
}


class AnyTickConsumer : TickConsumer {

    var consumer: TickConsumer? = null

    override fun register(clocks: Array<Clock>, callback: (ticks: Map<String, JsonObject>) -> Unit) {
        consumer = createListenerFor(clocks[0])
        consumer?.register(clocks, callback)
    }

    private fun createListenerFor(clock: Clock): TickConsumer {
        return when (clock.channel!!.type) {
            "rabbit" -> RabbitTickConsumer()
            "http" -> HttpTickConsumer()
            else -> throw NotImplementedError()
        }
    }

    override fun stop() {
        consumer?.stop()
    }
}


class HttpTickConsumer : TickConsumer {

    private var listenerTimer: Timer = Timer(true)

    override fun register(clocks: Array<Clock>, callback: (ticks: Map<String, JsonObject>) -> Unit) {
        val task = object : TimerTask() {
            override fun run() {
                val tick = getTicksFrom(clocks[0].channel!!.details["url"]!!)
                tick?.let {
                    callback(mapOf(clocks[0].id to tick))
                }
            }
        }
        listenerTimer.schedule(task, 0, 1000)
    }

    private fun getTicksFrom(url: String): JsonObject? {
        var tick: JsonObject? = null
        val response = Request.Get(url).execute().returnResponse()
        val content = EntityUtils.toString(response.entity)
        if (response.statusLine.statusCode != 200) {
            println("Error: $content")

        } else {
            val ticksJson = Gson().fromJson(content, JsonArray::class.java)
            if (ticksJson.size() > 0)
                tick = ticksJson[0].asJsonObject
        }
        return tick
    }

    override fun stop() {
        listenerTimer.cancel()
    }
}


class RabbitTickConsumer : TickConsumer {

    companion object {
        var connection: Connection? = null
        var channel: Channel? = null
    }

    private var consumerTag: String = ""


    override fun register(clocks: Array<Clock>, callback: (ticks: Map<String, JsonObject>) -> Unit) {
        createConnectionIfNeeded(clocks[0])
        val consumer = object : DefaultConsumer(channel) {
            override fun handleDelivery(consumerTag: String?, envelope: Envelope?,
                                        properties: AMQP.BasicProperties?, body: ByteArray?) {
                val bodyStr = body?.toString(Charset.forName("UTF-8"))
                callback(mapOf(clocks[0].id to Gson().fromJson(bodyStr, JsonObject::class.java)))
            }
        }
        consumerTag = channel!!.basicConsume(clocks[0].channel!!.details["queue"], true, consumer)

    }

    private fun createConnectionIfNeeded(clock: Clock) {
        if (connection == null) {
            connection = createConnection(clock.channel)
            channel = connection!!.createChannel()
        }
    }

    private fun createConnection(channel: ClockChannel?): Connection {
        val factory = ConnectionFactory()
        factory.setUri(channel?.details!!["uri"])
        return factory.newConnection()
    }

    override fun stop() {
        if (consumerTag.isNotEmpty()) {
            channel!!.basicCancel(consumerTag)
        }
    }
}
