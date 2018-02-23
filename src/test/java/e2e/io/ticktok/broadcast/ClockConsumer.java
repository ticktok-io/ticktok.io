package e2e.io.ticktok.broadcast;

import com.rabbitmq.client.*;
import org.junit.After;

import java.io.IOException;
import java.util.concurrent.*;

import static io.ticktok.broadcast.ClocksController.QUEUE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ClockConsumer {


    private final ClockListener listener = new ClockListener();

    public void receiveTheClock(String clockExpr) throws Exception {
        assertTrue(receivedMessageWithTopic(clockExpr));
    }

    private boolean receivedMessageWithTopic(String topic) throws IOException, TimeoutException, InterruptedException {
        CountDownLatch messageWasReceived = new CountDownLatch(1);
        listener.register(topic, (message) -> messageWasReceived.countDown());
        return messageWasReceived.await(3, TimeUnit.SECONDS);
    }

    @After
    public void tearDown() throws Exception {
        listener.stop();
    }

    private class ClockListener {
        private static final String EXCHANGE_NAME = "clock.exchange";

        private Channel channel;

        public void register(String clockExpr, MessageHandler handler) throws IOException, TimeoutException {
            if (channel == null) {
                channel = createChannel();
            }

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    handler.consume(new String(body, "UTF-8"));
                }
            };
            channel.basicConsume(QUEUE, consumer);
        }

        private Channel createChannel() throws IOException, TimeoutException {
            Channel channel = createConnection().createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            return channel;
        }

        private Connection createConnection() throws IOException, TimeoutException {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            return factory.newConnection();
        }

        public void stop() throws IOException, TimeoutException {
            if (channel != null) {
                channel.close();
            }
        }
    }

    private interface MessageHandler {
        void consume(String message);
    }

}
