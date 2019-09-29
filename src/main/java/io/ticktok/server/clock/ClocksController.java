package io.ticktok.server.clock;

import io.swagger.annotations.*;
import io.ticktok.server.clock.actions.ClockActionFactory;
import io.ticktok.server.clock.repository.RepositoryClocksFinder;
import io.ticktok.server.clock.repository.ClocksPurger;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.TickChannel;
import io.ticktok.server.tick.TickChannelCreator;
import io.ticktok.server.tick.TickChannelOperations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Api(tags = {"clocks"})
@RestController
@RequestMapping("/api/v1/clocks")
public class ClocksController {

    public static final int CACHE_TTL = 1;

    private final ClocksRepository clocksRepository;
    private final TickChannelOperations tickChannelOperations;
    private final ClocksFinder clocksFinder;


    public ClocksController(ClocksRepository clocksRepository,
                            TickChannelOperations tickChannelOperations) {
        this.clocksRepository = clocksRepository;
        this.tickChannelOperations = tickChannelOperations;
        this.clocksFinder = new CachedClocksFinder(new RepositoryClocksFinder(clocksRepository), CACHE_TTL);
    }

    @PostMapping
    @ApiOperation(value = "Create a new clock")
    @ResponseStatus(code = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 201,
                    message = "Clock created successfully",
                    responseHeaders = {@ResponseHeader(name = "Location", description = "Url to the newly created clock", response = String.class)}),
            @ApiResponse(code = 400,
                    message = "Bad request")})
    public ResponseEntity<ClockResourceV2> create(@Valid @RequestBody ClockRequest clockRequest) {
        log.info("CLOCK-REQUEST: {}", clockRequest.toString());
        Clock savedClock = clocksRepository.saveClock(clockRequest.getName(), clockRequest.getSchedule());
        TickChannel channel = new TickChannelCreator(tickChannelOperations).createFor(savedClock);
        return createdClockEntity(savedClock, channel);
    }

    private ResponseEntity<ClockResourceV2> createdClockEntity(Clock clock, TickChannel channel) {
        final ClockResourceV2 resource = ClockResourceV2.builder()
                .domain(host())
                .clock(clock)
                .channel(channel)
                .build();
        return ResponseEntity
                .created(withAuthToken(resource.getUrl(), userPrincipal()))
                .body(resource);
    }

    private String host() {
        HttpServletRequest currentRequest = currentRequest();
        return currentRequest.getRequestURL().toString().replaceAll(currentRequest.getRequestURI(), "");
    }

    private HttpServletRequest currentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
    }

    private Principal userPrincipal() {
        return currentRequest().getUserPrincipal();
    }

    private URI withAuthToken(String clockUrl, Principal principal) {
        return UriComponentsBuilder.fromUriString(clockUrl)
                .queryParam("access_token", principal.getName())
                .build().toUri();
    }

    @GetMapping("/{id}")
    @ApiOperation("Retrieve a specific clock")
    public ClockResourceV2 findOne(@PathVariable("id") String id) {
        return createClockResourceFor(new RepositoryClocksFinder(clocksRepository).findById(id));
    }

    private ClockResourceV2 createClockResourceFor(Clock clock) {
        return ClockResourceV2.builder().domain(host()).clock(clock).build();
    }

    @GetMapping
    @ApiOperation("Get all defined clocks")
    public List<ClockResourceV2> findAll(@RequestParam Map<String, String> queryParams) {
        return clocksFinder.findBy(queryParams)
                .stream().map(this::createClockResourceFor).collect(Collectors.toList());
    }
}

