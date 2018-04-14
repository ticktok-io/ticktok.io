package io.ticktok.server.clock;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class RabbitMQTickScheduler implements TickScheduler {
    public static final String CLOCK_QUEUE = "ticktok-queue";

    private final String rabbitUri;

    @Autowired
    public RabbitMQTickScheduler(@Value("${rabbit.uri}") String rabbitUri) {
        this.rabbitUri = rabbitUri;
    }

    @Override
    public ClockChannel scheduleFor(Clock clock) {
        new Thread(() -> {
            try {
                Connection connection = createConnection();
                Channel channel = connection.createChannel();
                channel.queueDeclare(CLOCK_QUEUE, false, false, true, new HashMap<>());
                channel.exchangeDeclare(ClockChannel.EXCHANGE_NAME, "topic");
                channel.queueBind(CLOCK_QUEUE, ClockChannel.EXCHANGE_NAME, clock.getSchedule());

                Thread.sleep(3);
                channel.basicPublish(ClockChannel.EXCHANGE_NAME, clock.getSchedule(), null, "".getBytes());
                connection.close();
            } catch (IOException | TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException | InterruptedException e) {
                log.error("Failed to invoke a tick for: {}", clock, e);
            }
        }).start();
        return new ClockChannel(rabbitUri, clock.getSchedule());
    }

    private Connection createConnection() throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException, IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(rabbitUri);
        return factory.newConnection();
    }
}
