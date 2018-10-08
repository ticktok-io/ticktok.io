package e2e.test.io.ticktok.server.support

import com.rabbitmq.client.*
import org.junit.jupiter.api.fail
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class TickListener {

    companion object {
        const val CLOCK_EXPR = "every.3.seconds"
        const val QUEUE = "tick-client"
    }

    fun receivedTicksFor(clock: Clock) {
        SingleTickLatch(clock.channel!!).awaitFor(3)
        SingleTickLatch(clock.channel!!).awaitFor(3)
    }

    private inner class SingleTickLatch(val clockChannel: ClockChannel) {

        private var tickReceived = CountDownLatch(1)

        fun awaitFor(timeout: Long) {
            var channel: Channel? = null
            try {
                channel = createChannelFor()
                val consumer = object : DefaultConsumer(channel) {
                    override fun handleDelivery(consumerTag: String?, envelope: Envelope?,
                                                properties: AMQP.BasicProperties?, body: ByteArray?) {
                        tickReceived.countDown()
                    }
                }
                channel!!.basicConsume(QUEUE, true, consumer)
                if (!tickReceived.await(timeout + 1, TimeUnit.SECONDS)) {
                    fail("Failed to receive tick for $clockChannel")
                }
            } finally {
                closeChannel(channel)
            }
        }

        private fun createChannelFor(): Channel? {
            val channel = createConnection().createChannel()
            channel.exchangeDeclare(clockChannel.exchange, "topic")
            channel.queueDeclare(QUEUE, false, false, true, HashMap())
            channel.queueBind(QUEUE, clockChannel.exchange, clockChannel.topic)
            return channel
        }

        private fun createConnection(): Connection {
            val factory = ConnectionFactory()
            factory.setUri(clockChannel.uri)
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