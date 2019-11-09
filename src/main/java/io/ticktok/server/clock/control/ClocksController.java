package io.ticktok.server.clock.control;

import io.swagger.annotations.*;
import io.ticktok.server.clock.CachedClocksFinder;
import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.ClocksFinder;
import io.ticktok.server.clock.actions.ClockActionFactory;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.clock.repository.RepositoryClocksFinder;
import io.ticktok.server.tick.TickChannel;
import io.ticktok.server.tick.TickChannelCreator;
import io.ticktok.server.tick.TickChannelOperations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.ticktok.server.clock.control.HttpRequestUtil.userPrincipal;

@Slf4j
@Api(tags = {"clocks"})
@RestController
@RequestMapping("/api/v1/clocks")
public class ClocksController {

    public static final int CACHE_TTL = 1;

    private final ClocksRepository clocksRepository;
    private final TickChannelOperations tickChannelOperations;
    private final ClocksFinder clocksFinder;
    private final ClockResourceFactory clockResourceFactory;


    public ClocksController(ClocksRepository clocksRepository,
                            TickChannelOperations tickChannelOperations,
                            ClockActionFactory clockActionFactory) {
        this.clocksRepository = clocksRepository;
        this.tickChannelOperations = tickChannelOperations;
        this.clocksFinder = new CachedClocksFinder(new RepositoryClocksFinder(clocksRepository), CACHE_TTL);
        this.clockResourceFactory = new ClockResourceFactory(clockActionFactory);
    }

    @PostMapping
    @ApiOperation(value = "Create a new clock")
    @ResponseStatus(code = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 201,
                    message = "Clock created successfully",
                    responseHeaders = {@ResponseHeader(
                            name = "Location",
                            description = "Url to the newly created clock",
                            response = String.class)}),
            @ApiResponse(code = 400,
                    message = "Bad request")})
    public ResponseEntity<ClockResource> create(@Valid @RequestBody ClockRequest clockRequest) {
        log.info("CLOCK-REQUEST: {}", clockRequest.toString());
        Clock savedClock = clocksRepository.saveClock(clockRequest.getName(), clockRequest.getSchedule());
        TickChannel channel = new TickChannelCreator(tickChannelOperations).createFor(savedClock);
        return createdClockEntity(savedClock, channel);
    }

    private ResponseEntity<ClockResource> createdClockEntity(Clock clock, TickChannel channel) {
        ClockResource resource = clockResourceFactory.createWithChannel(clock, channel);
        return ResponseEntity
                .created(withAuthToken(resource.getId().getHref(), userPrincipal()))
                .body(resource);
    }

    private URI withAuthToken(String clockUrl, Principal principal) {
        return UriComponentsBuilder.fromUriString(clockUrl)
                .queryParam("access_token", principal.getName())
                .build().toUri();
    }

    @GetMapping
    @ApiOperation("Get all defined clocks")
    public List<ClockResource> findAll(@RequestParam Map<String, String> queryParams) {
        return clocksFinder.findBy(queryParams)
                .stream().map(clockResourceFactory::create).collect(Collectors.toList());
    }
}

