package e2e.test.io.ticktok.server.support

import com.rabbitmq.client.*
import org.junit.jupiter.api.fail
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class TickListener {

    companion object {
        const val CLOCK_EXPR = "every.2.seconds"
    }

    fun receivedTicksFor(clock: Clock) {
        val connection = createConnection(clock.channel)
        val channel = connection.createChannel()
        try {
            SingleTickLatch(channel, clock.channel?.queue).awaitFor(2)
        } finally {
            closeChannel(channel)
            connection.close()
        }
    }

    private fun createConnection(channel: ClockChannel?): Connection {
        val factory = ConnectionFactory()
        factory.setUri(channel?.uri)
        return factory.newConnection()
    }

    private fun closeChannel(channel: Channel?) {
        if (isOpen(channel)) {
            channel!!.close()
        }
    }

    private fun isOpen(channel: Channel?) = channel != null && channel.isOpen

    private inner class SingleTickLatch(val channel: Channel, val queue: String?) {


        fun awaitFor(times: Int) {
            val tickReceived = CountDownLatch(times)
            var consumerTag = ""
            try {
                val consumer = object : DefaultConsumer(channel) {
                    override fun handleDelivery(consumerTag: String?, envelope: Envelope?,
                                                properties: AMQP.BasicProperties?, body: ByteArray?) {
                        tickReceived.countDown()
                    }
                }
                consumerTag = channel.basicConsume(queue, true, consumer)
                if (!tickReceived.await((2 * times).toLong() + times, TimeUnit.SECONDS)) {
                    fail("Failed to receive ticks for $queue")
                }
            } finally {
                if(!consumerTag.isEmpty()) {
                    channel.basicCancel(consumerTag)
                }
            }
        }

    }

}