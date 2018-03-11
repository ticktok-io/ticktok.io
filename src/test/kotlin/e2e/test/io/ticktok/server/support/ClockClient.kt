package e2e.test.io.ticktok.server.support

import com.rabbitmq.client.*
import io.ticktok.server.ClocksController.QUEUE
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ClockClient {
    private val listener = ClockListener()

    fun receivedTheClock(clockExpr: String) {
        assertTrue(receivedMessageWithTopic(clockExpr))
    }

    private fun receivedMessageWithTopic(topic: String): Boolean {
        val messageWasReceived = CountDownLatch(1)
        listener.register(topic, { messageWasReceived.countDown() })
        return messageWasReceived.await(3, TimeUnit.SECONDS)
    }

    fun stop() {
        listener.stop()
    }

    private inner class ClockListener {

        private val EXCHANGE_NAME = "clock.exchange"
        private var channel: Channel? = null

        fun register(clockExpr: String, handler: (m: String) -> Unit) {
            if (channel == null) {
                channel = createChannel()
            }

            val consumer = object : DefaultConsumer(channel) {
                @Throws(IOException::class)
                override fun handleDelivery(consumerTag: String?, envelope: Envelope?,
                                            properties: AMQP.BasicProperties?, body: ByteArray?) {
                    handler(String(body!!, Charset.forName("UTF-8")))
                }
            }
            channel!!.basicConsume(QUEUE, consumer)
        }

        private fun createChannel(): Channel {
            val channel = createConnection().createChannel()
            channel.exchangeDeclare(EXCHANGE_NAME, "topic")
            return channel
        }

        private fun createConnection(): Connection {
            val factory = ConnectionFactory()
            factory.host = "localhost"
            return factory.newConnection()
        }

        fun stop() {
            if (channel != null && channel!!.isOpen) {
                channel!!.close()
            }
        }

    }

}