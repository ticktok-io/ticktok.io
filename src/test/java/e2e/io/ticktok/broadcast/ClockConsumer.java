package e2e.io.ticktok.broadcast;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.*;

public class ClockConsumer {

    public void receiveClock() throws ExecutionException, InterruptedException, TimeoutException {
        Executors.newSingleThreadExecutor().submit(new ClockListener()).get(3, TimeUnit.SECONDS);
    }

    class ClockListener implements Callable<String> {

        private static final String EXCHANGE_NAME = "e2e-exchange";
        public static final String MY_QUEUE = "e2e-clock";

        private String message;

        @Override
        public String call() throws Exception {
            Channel channel = createChannel();

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    message = new String(body, "UTF-8");
                    channel.basicCancel(consumerTag);
                }
            };
            channel.basicConsume(MY_QUEUE, consumer);
            return message;
        }

        private Channel createChannel() throws IOException, TimeoutException {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            channel.queueBind(MY_QUEUE, EXCHANGE_NAME, "every.3.minutes");
            return channel;
        }
    }
}
