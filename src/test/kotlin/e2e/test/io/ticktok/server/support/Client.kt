package e2e.test.io.ticktok.server.support

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.rabbitmq.client.*
import org.apache.http.client.fluent.Request
import org.awaitility.Duration
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import java.lang.Thread.sleep
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer
import kotlin.test.assertTrue


object Client {

    const val CLOCK_EXPR = "every.2.seconds"
    private val listeners = hashMapOf<String, TickListener>()

    fun receivedTicksFor(clock: Clock) {
        await atMost Duration(3, TimeUnit.SECONDS) until { listeners[clock.id]?.messages?.isNotEmpty()!! }
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

    abstract class TickListener(val clock: Clock) {
        val messages: MutableList<String> = mutableListOf()

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
                    messages.add(body.toString())
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

        var listenerTimer: Timer? = null

        override fun listenOn(clock: Clock) {
            listenerTimer = timer(name = clock.id, period = 1000) {
                object : TimerTask() {
                    override fun run() {
                        val content = Request.Get("${clock.url}/${clock.channel!!.details["url"]}?access_tocken=${App.ACCESS_TOKEN}")
                                .execute()
                                .returnContent().asString()
                        val ticksJson = Gson().fromJson(content, JsonObject::class.java)
                        val ticks = ticksJson.getAsJsonArray()
                        ticks.forEach { t -> messages.add(t.asJsonObject.get("payload").asString) }
                    }
                }
            }
        }

        override fun stop() {
            listenerTimer?.cancel()
        }
    }

    class NoTickListener(clock: Clock) : TickListener(clock) {

        override fun listenOn(clock: Clock) {
            throw NotImplementedError()
        }

    }

}