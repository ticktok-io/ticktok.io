package io.ticktok.server;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

@Api(tags = {"clocks"})
@RestController
@RequestMapping("/api/v1/clocks")
public class ClocksController {

    public static final String CLOCK_EXPR = "once.in.4.seconds";
    public static final String QUEUE = "e2e-clock";

    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private final String rabbitUri;
    private final ClocksRepository clocksRepository;
    private final String domain;


    public ClocksController(@Value("${rabbit.uri}") String rabbitUri,
                            @Value("${http.domain}") String domain,
                            ClocksRepository clocksRepository) {
        this.rabbitUri = rabbitUri;
        this.domain = domain;
        this.clocksRepository = clocksRepository;
    }

    @PostMapping
    @ApiOperation("Create a new clock")
    public ResponseEntity<Clock> create(@RequestBody Clock clock, Principal principal) {
        Clock savedClock = clocksRepository.save(clock);
        worker.submit(new Runnable() {
            private static final String EXCHANGE_NAME = "clock.exchange";

            @Override
            public void run() {
                ConnectionFactory factory = new ConnectionFactory();
                try {
                    factory.setUri(rabbitUri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                }
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
        return ResponseEntity.created(
                createUriFor(savedClock, principal))
                .body(savedClock);
    }

    private URI createUriFor(Clock savedClock, Principal principal) {
        return UriComponentsBuilder.fromHttpUrl(domain)
                .path("/api/v1/clocks/{id}")
                .queryParam("access_token", principal.getName())
                .buildAndExpand(savedClock.getId()).toUri();
    }

    @GetMapping("/{id}")
    public Clock findOne(@PathVariable("id") String id) {
        return clocksRepository.findOne(id);
    }

    @GetMapping
    @ApiOperation("Get all defined clocks")
    public List<Clock> findAll() {
        return clocksRepository.findAll();
    }

}
