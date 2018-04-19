package e2e.test.io.ticktok.server.support

import com.rabbitmq.client.*
import org.junit.jupiter.api.fail
import java.util.HashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class ClockClient {

    companion object {
        const val QUEUE = "tick-client"
    }

    fun receivedTickFor(clock: Clock) {
        SingleTickLatch(clock.channel!!).await()
    }

    private inner class SingleTickLatch(val clockChannel: ClockChannel) {

        private var tickReceived = CountDownLatch(1)

        fun await() {
            var channel: Channel? = null
            try {
                channel = createChannel()
                val consumer = object : DefaultConsumer(channel) {
                    override fun handleDelivery(consumerTag: String?, envelope: Envelope?,
                                                properties: AMQP.BasicProperties?, body: ByteArray?) {
                        tickReceived.countDown()
                    }
                }
                channel!!.basicConsume(QUEUE, true, consumer)
                println("Listening...")
                if (!tickReceived.await(4, TimeUnit.SECONDS)) {
                    fail("Failed to receive tick for $clockChannel")
                }
            } finally {
                closeChannel(channel)
            }
        }

        private fun createChannel(): Channel? {
            val channel = createConnection().createChannel()
            channel.exchangeDeclare(clockChannel.exchange, "topic")
            channel.queueDeclare(QUEUE, false, false, true, HashMap())
            channel.queueBind(QUEUE, clockChannel.exchange, clockChannel.topic)
            return channel
        }

        private fun createConnection(): Connection {
            val factory = ConnectionFactory()
            factory.host = "localhost"
            return factory.newConnection()
        }

        private fun closeChannel(channel: Channel?) {
            if (isOpen(channel)) {
                channel!!.close()
            }
        }

        private fun isOpen(channel: Channel?) = channel != null && channel.isOpen

    }

}