package e2e.test.io.ticktok.server.support

import com.rabbitmq.client.*
import org.junit.jupiter.api.fail
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class TickListener {

    companion object {
        const val CLOCK_EXPR = "every.3.seconds"
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
                channel = createConnection().createChannel()
                val consumer = object : DefaultConsumer(channel) {
                    override fun handleDelivery(consumerTag: String?, envelope: Envelope?,
                                                properties: AMQP.BasicProperties?, body: ByteArray?) {
                        tickReceived.countDown()
                    }
                }
                channel!!.basicConsume(clockChannel.queue, true, consumer)
                if (!tickReceived.await(timeout + 1, TimeUnit.SECONDS)) {
                    fail("Failed to receive tick for $clockChannel")
                }
            } finally {
                closeChannel(channel)
            }
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