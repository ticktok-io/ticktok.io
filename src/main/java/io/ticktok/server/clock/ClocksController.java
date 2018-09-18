package io.ticktok.server.clock;

import io.swagger.annotations.*;
import io.ticktok.server.tick.ScheduleParser;
import io.ticktok.server.tick.TickChannelFactory;
import io.ticktok.server.tick.TickPublisher;
import io.ticktok.server.tick.TickScheduler2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Api(tags = {"clocks"})
@RestController
@RequestMapping("/api/v1/clocks")
public class ClocksController {

    private final ClocksRepository clocksRepository;
    private final String domain;
    private final TickChannelFactory tickChannelFactory;
    private final TickPublisher tickPublisher;


    public ClocksController(@Value("${http.domain}") String domain,
                            ClocksRepository clocksRepository,
                            TickPublisher tickPublisher,
                            TickChannelFactory tickChannelFactory) {
        this.domain = domain;
        this.clocksRepository = clocksRepository;
        this.tickPublisher = tickPublisher;
        this.tickChannelFactory = tickChannelFactory;
    }

    @PostMapping
    @ApiOperation(value = "Create a new clock")
    @ResponseStatus(code = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Clock created successfully", responseHeaders = {@ResponseHeader(name = "Location", description = "Url to the newly created clock", response = String.class)})
    })
    public ResponseEntity<ClockResourceWithChannel> create(@RequestBody ClockDetails clockDetails, Principal principal) {
        Clock savedClock = clocksRepository.save(Clock.createFrom(clockDetails));
        //new TickScheduler2(tickPublisher).scheduleFor(savedClock);
        return createdClockEntity(savedClock, principal);
    }

    private ResponseEntity<ClockResourceWithChannel> createdClockEntity(Clock clock, Principal principal) {
        ClockResourceWithChannel clockResource =
                new ClockResourceWithChannel(domain, clock, tickChannelFactory.createForSchedule(clock.getSchedule()));
        return ResponseEntity.created(
                withAuthToken(clockResource.getUrl(), principal))
                .body(clockResource);
    }

    private URI withAuthToken(String clockUrl, Principal principal) {
        return UriComponentsBuilder.fromUriString(clockUrl)
                .queryParam("access_token", principal.getName())
                .build().toUri();
    }

    @GetMapping("/{id}")
    @ApiOperation("Retrieve a specific clock")
    public ClockDetails findOne(@PathVariable("id") String id) {
        Optional<Clock> clock = clocksRepository.findById(id);
        return createClockResourceFor(clock.get());
    }

    private ClockResource createClockResourceFor(Clock clock) {
        return new ClockResource(domain, clock);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Delete a specific clock")
    public void deleteOne(@PathVariable("id") String id) {
        clocksRepository.deleteById(id);
    }

    @GetMapping
    @ApiOperation("Get all defined clocks")
    public List<ClockResource> findAll() {
        return clocksRepository.findAll().stream().map(this::createClockResourceFor).collect(Collectors.toList());
    }

    @ResponseStatus(value=HttpStatus.BAD_REQUEST,
            reason="Non valid schedule expression")
    @ExceptionHandler(ScheduleParser.ExpressionNotValidException.class)
    public void badSchedule() {
        // Nothing to do
    }

}
