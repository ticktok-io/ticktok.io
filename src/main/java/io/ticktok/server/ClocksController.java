package io.ticktok.server;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
import java.util.stream.Collectors;

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
    @ApiOperation(value="Create a new clock")
    @ResponseStatus(code = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Clock created successfully", responseHeaders = {@ResponseHeader(name = "Location", description = "Url to the newly created clock", response = String.class)})
    })
    public ResponseEntity<ClockResource> create(@RequestBody ClockDetails clockDetails, Principal principal) {
        Clock savedClock = clocksRepository.save(Clock.createFrom(clockDetails));
        worker.submit(new Runnable() {
            private static final String EXCHANGE_NAME = "clockRequest.exchange";

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
        ClockResource clockResource = new ClockResource(domain, savedClock);
        return ResponseEntity.created(
                withAuthToken(clockResource.getUrl(), principal))
                .body(new ClockResource(domain, savedClock));
    }

    private URI withAuthToken(String clockUrl, Principal principal) {
        return UriComponentsBuilder.fromUriString(clockUrl)
                .queryParam("access_token", principal.getName())
                .build().toUri();
    }

    @GetMapping("/{id}")
    @ApiOperation("Retrieve a specific clock")
    public ClockDetails findOne(@PathVariable("id") String id) {
        Clock clock = clocksRepository.findOne(id);
        return new ClockResource(domain, clock);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Delete a specific clock")
    public void deleteOne(@PathVariable("id") String id) {
        clocksRepository.delete(id);
    }

    @GetMapping
    @ApiOperation("Get all defined clocks")
    public List<ClockResource> findAll() {
        return clocksRepository.findAll().stream().map(c ->
                new ClockResource(domain, c)).collect(Collectors.toList());
    }

}
