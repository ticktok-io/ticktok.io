package io.ticktok.server.clock;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Api(tags = {"clocks"})
@RestController
@RequestMapping("/api/v1/clocks")
public class ClocksController {

    public static final String CLOCK_EXPR = "once.in.4.seconds";
    public static final String CLOCK_QUEUE = "ticktok-queue";

    private final ExecutorService worker = Executors.newSingleThreadExecutor();

    private final ClocksRepository clocksRepository;
    private final String domain;
    private final String rabbitUri;


    public ClocksController(@Value("${rabbit.uri}") String rabbitUri,
                            @Value("${http.domain}") String domain,
                            ClocksRepository clocksRepository) {
        this.domain = domain;
        this.rabbitUri = rabbitUri;
        this.clocksRepository = clocksRepository;
    }

    @PostMapping
    @ApiOperation(value = "Create a new clock")
    @ResponseStatus(code = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Clock created successfully", responseHeaders = {@ResponseHeader(name = "Location", description = "Url to the newly created clock", response = String.class)})
    })
    public ResponseEntity<ClockResource> create(@RequestBody ClockDetails clockDetails, Principal principal) {
        Clock savedClock = clocksRepository.save(Clock.createFrom(clockDetails));
        worker.submit(() -> {
            try {
                Connection connection = createConnection();
                Channel channel = connection.createChannel();
                channel.queueDeclare(CLOCK_QUEUE, false, false, true, new HashMap<>());
                channel.exchangeDeclare(ClockChannel.EXCHANGE_NAME, "topic");
                channel.queueBind(CLOCK_QUEUE, ClockChannel.EXCHANGE_NAME, CLOCK_EXPR);

                channel.basicPublish(ClockChannel.EXCHANGE_NAME, CLOCK_EXPR, null, "".getBytes());
                connection.close();
            } catch (IOException | TimeoutException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException e) {
                log.error("Failed to connect to the queue", e);
            }
        });
        ClockResource clockResource = createClockResourceFor(savedClock);
        return ResponseEntity.created(
                withAuthToken(clockResource.getUrl(), principal))
                .body(createClockResourceFor(savedClock));
    }

    private Connection createConnection() throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException, IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(rabbitUri);
        return factory.newConnection();
    }

    private URI withAuthToken(String clockUrl, Principal principal) {
        return UriComponentsBuilder.fromUriString(clockUrl)
                .queryParam("access_token", principal.getName())
                .build().toUri();
    }

    private ClockResource createClockResourceFor(Clock clock) {
        return new ClockResource(domain, clock, rabbitUri);
    }

    @GetMapping("/{id}")
    @ApiOperation("Retrieve a specific clock")
    public ClockDetails findOne(@PathVariable("id") String id) {
        Clock clock = clocksRepository.findOne(id);
        return createClockResourceFor(clock);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Delete a specific clock")
    public void deleteOne(@PathVariable("id") String id) {
        clocksRepository.delete(id);
    }

    @GetMapping
    @ApiOperation("Get all defined clocks")
    public List<ClockResource> findAll() {
        return clocksRepository.findAll().stream().map(this::createClockResourceFor).collect(Collectors.toList());
    }

}
