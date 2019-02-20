package e2e.test.io.ticktok.server.support

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.rabbitmq.client.*
import org.apache.http.client.fluent.Request
import org.apache.http.util.EntityUtils
import org.assertj.core.api.Assertions
import org.awaitility.Duration
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.awaitility.kotlin.untilNotNull
import java.lang.Thread.sleep
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue


object Client {

    const val CLOCK_EXPR = "every.2.seconds"
    private val listeners = hashMapOf<String, TickListener>()

    fun receivedTicksFor(clock: Clock) {
        await atMost Duration(4, TimeUnit.SECONDS) until {
            listeners[clock.id]?.messages?.isNotEmpty()!! &&
                    listeners[clock.id]?.messages?.filter { m -> m.get("schedule").asString == clock.schedule }!!.any()
        }
    }

    fun receivesNoMoreTicks() {
        sleep(2000)
        reset()
        sleep(2000)
        listeners.values.forEach { v ->
            assertTrue(v.messages.isEmpty(), "received ticks for ${v.name()}")
        }
    }

    fun reset() {
        listeners.values.forEach { v -> v.clear() }
    }

    fun startListenTo(clock: Clock) {
        if (!listeners.contains(clock.id)) {
            listeners[clock.id] = createListenerFor(clock)
            listeners[clock.id]?.start()
        }
    }

    fun createListenerFor(clock: Clock): TickListener {
        return when (clock.channel!!.type) {
            "rabbit" -> RabbitTickListener(clock)
            "http" -> HttpTickListener(clock)
            else -> NoTickListener(clock)
        }
    }

    fun stop() {
        stopAllListeners()
    }

    private fun stopAllListeners() {
        listeners.values.forEach { v -> v.stop() }
        listeners.clear()
    }

    fun failToFindQueue() {
        await atMost Duration(4, TimeUnit.SECONDS) untilNotNull  { firstListenerWithError() }
        Assertions.assertThat(firstListenerWithError()!!.errors[0]).contains("Error: 404")
    }

    fun firstListenerWithError() : TickListener? {
        return listeners.values.find { l -> !l.errors.isEmpty() }
    }

    abstract class TickListener(val clock: Clock) {
        val messages: MutableList<JsonObject> = Collections.synchronizedList(mutableListOf())
        val errors: MutableList<String> = Collections.synchronizedList(mutableListOf())

        fun start() {
            listenOn(clock)
        }

        abstract fun listenOn(clock: Clock)

        open fun name(): String {
            return "${clock.id} - ${clock.name} - ${clock.schedule}"
        }

        open fun stop() {
            // do nothing by default
        }

        fun clear() {
            messages.clear()
        }
    }

    class RabbitTickListener(clock: Clock) : TickListener(clock) {

        companion object {
            var connection: Connection? = null
            var channel: Channel? = null
        }

        var consumerTag: String = ""

        override fun listenOn(clock: Clock) {
            createConnectionIfNeeded()
            val consumer = object : DefaultConsumer(channel) {
                override fun handleDelivery(consumerTag: String?, envelope: Envelope?,
                                            properties: AMQP.BasicProperties?, body: ByteArray?) {
                    val bodyStr = body?.toString(Charset.forName("UTF-8"))
                    messages.add(Gson().fromJson(bodyStr, JsonObject::class.java))
                }
            }
            consumerTag = channel!!.basicConsume(clock.channel!!.details["queue"], true, consumer)
        }

        private fun createConnectionIfNeeded() {
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
            if (!consumerTag.isEmpty()) {
                channel!!.basicCancel(consumerTag)
            }
        }
    }

    class HttpTickListener(clock: Clock) : TickListener(clock) {

        var listenerTimer: Timer = Timer(true);

        override fun listenOn(clock: Clock) {
            val task = object : TimerTask() {
                override fun run() {
                    val url = "${App.appUrl}${clock.channel!!.details["path"]}?access_token=${App.ACCESS_TOKEN}"
                    val response = Request.Get(url).execute().returnResponse()
                    val content = EntityUtils.toString(response.entity)
                    if(response.statusLine.statusCode != 200) {
                        errors.add("Error: ${response.statusLine.statusCode} [$content]")
                    } else {
                        val ticksJson = Gson().fromJson(content, JsonArray::class.java)
                        val ticks = ticksJson.asJsonArray
                        ticks.forEach { t -> messages.add(t.asJsonObject) }
                    }
                }
            }
            listenerTimer.schedule(task, 0, 1000)
        }

        override fun stop() {
            listenerTimer.cancel()
        }
    }

    class NoTickListener(clock: Clock) : TickListener(clock) {

        override fun listenOn(clock: Clock) {
            throw NotImplementedError()
        }
    }

}