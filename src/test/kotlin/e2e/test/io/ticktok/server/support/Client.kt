package e2e.test.io.ticktok.server.support

import com.rabbitmq.client.*
import org.assertj.core.api.Assertions
import org.awaitility.Duration
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.junit.jupiter.api.fail
import java.lang.Thread.sleep
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue


object Client {

    const val CLOCK_EXPR = "every.2.seconds"
    private var connection: Connection? = null
    private var channel: Channel? = null
    private val listeners = hashMapOf<String, TickListener>()

    fun receivedTicksFor(clock: Clock) {
        await atMost Duration.TWO_SECONDS until { listeners[clock.id]?.messages?.isNotEmpty()!! }
    }

    fun receivesNoMoreTicks() {
        sleep(1000)
        reset()
        sleep(2000)
        listeners.values.forEach { v ->
            assertTrue(v.messages.isEmpty(), "recieved ticks for ${v.queue}")
        }
    }

    fun reset() {
        listeners.values.forEach { v -> v.clear() }
    }

    fun startListenTo(clock: Clock) {
        if (connection == null) {
            connection = createConnection(clock.channel)
            channel = connection!!.createChannel()
        }
        startTickListenerFor(clock)
    }

    private fun createConnection(channel: ClockChannel?): Connection {
        val factory = ConnectionFactory()
        factory.setUri(channel?.uri)
        return factory.newConnection()
    }

    private fun startTickListenerFor(clock: Clock) {
        if (!listeners.contains(clock.id)) {
            listeners[clock.id] = TickListener(channel!!, clock.channel!!.queue)
            listeners[clock.id]?.start()
        }
    }

    fun stop() {
        stopAllListeners()
        if (connection != null) {
            connection!!.close()
            connection = null
        }
        closeChannel()
    }

    private fun stopAllListeners() {
        listeners.values.forEach { v -> v.stop() }
        listeners.clear()
    }

    private fun closeChannel() {
        if (isOpen(channel)) {
            channel!!.close()
            channel = null
        }
    }

    private fun isOpen(channel: Channel?) = channel != null && channel.isOpen

    class TickListener(val channel: Channel, val queue: String) {

        val messages: MutableList<String> = mutableListOf()
        var consumerTag: String = ""

        fun start() {
            val consumer = object : DefaultConsumer(channel) {
                override fun handleDelivery(consumerTag: String?, envelope: Envelope?,
                                            properties: AMQP.BasicProperties?, body: ByteArray?) {
                    messages.add(body.toString())
                }
            }
            consumerTag = channel.basicConsume(queue, true, consumer)
        }

        fun stop() {
            if (!consumerTag.isEmpty()) {
                channel.basicCancel(consumerTag)
            }
        }

        fun clear() {


        }
    }

}