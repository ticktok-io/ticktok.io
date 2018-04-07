package e2e.test.io.ticktok.server.support

import com.rabbitmq.client.*
import java.io.IOException
import java.util.HashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ClockClient {

    companion object {
        const val QUEUE = "tick-client"
    }

    fun receivedTheClock(clock: Clock) {
        SingleTickLatch(clock.channel).await()
    }

    private inner class SingleTickLatch(val clockChannel: ClockChannel) {

        private var tickReceived = CountDownLatch(1)

        fun await() {
            var channel: Channel? = null
            try {
                channel = createChannel()

                val consumer = object : DefaultConsumer(channel) {
                    @Throws(IOException::class)
                    override fun handleDelivery(consumerTag: String?, envelope: Envelope?,
                                                properties: AMQP.BasicProperties?, body: ByteArray?) {
                        tickReceived.countDown()
                    }
                }
                channel.basicConsume(QUEUE, consumer)
                tickReceived.await(3, TimeUnit.SECONDS)
            } finally {
                closeChannel(channel)
            }
        }

        private fun closeChannel(channel: Channel?) {
            if (channel != null && channel.isOpen) {
                channel.close()
            }
        }

        private fun createChannel(): Channel {
            val channel = createConnection().createChannel()
            channel.queueDeclare(QUEUE, false, false, true, HashMap())
            channel.exchangeDeclare(clockChannel.exchange, "topic")
            channel.queueBind(QUEUE, clockChannel.exchange, clockChannel.topic)
            return channel
        }

        private fun createConnection(): Connection {
            val factory = ConnectionFactory()
            factory.setUri(clockChannel.uri)
            return factory.newConnection()
        }

    }

}