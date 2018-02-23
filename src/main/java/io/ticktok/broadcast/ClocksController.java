package io.ticktok.broadcast;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/v1/clocks")
public class ClocksController {

    public static final String CLOCK_EXPR = "once.in.4.seconds";

    public static final String QUEUE = "e2e-clock";
    private final ExecutorService worker = Executors.newSingleThreadExecutor();

    @PostMapping
    public ResponseEntity<Void> create() {
        worker.submit(new Runnable() {
            private static final String EXCHANGE_NAME = "clock.exchange";

            @Override
            public void run() {
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost("localhost");
                try {
                    Connection connection = factory.newConnection();
                    Channel channel = connection.createChannel();
                    channel.queueDeclare(QUEUE, false, false, true, new HashMap<>());
                    channel.exchangeDeclare(EXCHANGE_NAME, "topic");
                    channel.queueBind(QUEUE, EXCHANGE_NAME, CLOCK_EXPR);

                    channel.basicPublish(EXCHANGE_NAME, CLOCK_EXPR, null, "".getBytes());
                    connection.close();
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        });
        return ResponseEntity.created(URI.create("")).build();
    }

}
