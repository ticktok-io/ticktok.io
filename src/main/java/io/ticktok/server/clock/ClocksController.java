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
    private final ClocksPurger clocksPurger;
    private final ClockActionFactory clockActionFactory;


    public ClocksController(ClocksRepository clocksRepository,
                            TickChannelOperations tickChannelOperations,
                            ClocksPurger clocksPurger,
                            ClockActionFactory clockActionFactory) {
        this.clocksRepository = clocksRepository;
        this.tickChannelOperations = tickChannelOperations;
        this.clocksPurger = clocksPurger;
        this.clockActionFactory = clockActionFactory;
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
    public ResponseEntity<ClockResourceWithChannel> create(@Valid @RequestBody ClockRequest clockRequest) {
        log.info("CLOCK-REQUEST: {}", clockRequest.toString());
        Clock savedClock = clocksRepository.saveClock(clockRequest.getName(), clockRequest.getSchedule());
        TickChannel channel = new TickChannelCreator(tickChannelOperations).createFor(savedClock);
        return createdClockEntity(savedClock, channel);
    }

    private ResponseEntity<ClockResourceWithChannel> createdClockEntity(Clock clock, TickChannel channel) {
        ClockResourceWithChannel clockResource =
                new ClockResourceWithChannel(host(), clock, channel);
        return ResponseEntity.created(
                withAuthToken(clockResource.getUrl(), userPrincipal()))
                .body(clockResource);
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
    public ClockResource findOne(@PathVariable("id") String id) {
        return createClockResourceFor(new RepositoryClocksFinder(clocksRepository).findById(id));
    }

    private ClockResource createClockResourceFor(Clock clock) {
        return new ClockResource(host(), clock);
    }

    @GetMapping
    @ApiOperation("Get all defined clocks")
    public List<ClockResource> findAll(@RequestParam Map<String, String> queryParams) {
        return findClocksBy(queryParams).stream().map(this::createClockResourceFor).collect(Collectors.toList());
    }

    private List<Clock> findClocksBy(@RequestParam Map<String, String> queryParams) {
        return new CachedClocksFinder(new RepositoryClocksFinder(clocksRepository), CACHE_TTL).findBy(queryParams);
    }

    @PostMapping("/purge")
    @ApiOperation("Purge clocks with no active schedules")
    public ResponseEntity<Void> purge() {
        clocksPurger.purge();
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/{action}")
    @ApiOperation(value = "Run an action on a specific clock")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> clockAction(
            @PathVariable String id,
            @ApiParam(required = true, allowableValues = "pause,resume,tick") @PathVariable String action) {
        log.info("CLOCK-ACTION: {} on clock: {}", action, id);
        clockActionFactory.run(action, id);
        return ResponseEntity.noContent().build();
    }

}

