package e2e.io.ticktok.broadcast;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.*;

public class ClockConsumer {

    public void receivedClock() throws ExecutionException, InterruptedException, TimeoutException {
        Executors.newSingleThreadExecutor().submit(new ClockListener()).get(3, TimeUnit.SECONDS);
    }

    class ClockListener implements Callable<String> {

        private static final String EXCHANGE_NAME = "ct_clock";

        private String message;

        @Override
        public String call() throws Exception {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            channel.queueBind("myqueue", EXCHANGE_NAME, "every.3.minutes");

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    message = new String(body, "UTF-8");
                    channel.basicCancel(consumerTag);
                }
            };
            channel.basicConsume("myqueue", consumer);
            return message;
        }
    }
}
