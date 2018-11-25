package io.ticktok.server.clock;

import io.swagger.annotations.*;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.TickChannel;
import io.ticktok.server.tick.TickChannelCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Api(tags = {"clocks"})
@RestController
@RequestMapping("/api/v1/clocks")
public class ClocksController {

    private final ClocksRepository clocksRepository;
    private final String domain;
    private final TickChannelCreator tickChannelCreator;
    private final ClocksPurger clocksPurger;


    public ClocksController(@Value("${http.domain}") String domain,
                            ClocksRepository clocksRepository,
                            TickChannelCreator tickChannelCreator,
                            ClocksPurger clocksPurger) {
        this.domain = domain;
        this.clocksRepository = clocksRepository;
        this.tickChannelCreator = tickChannelCreator;
        this.clocksPurger = clocksPurger;
    }

    @PostMapping
    @ApiOperation(value = "Create a new clock")
    @ResponseStatus(code = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 201,
                    message = "Clock created successfully",
                    responseHeaders = {@ResponseHeader(name = "Location", description = "Url to the newly created clock", response = String.class)})
    })
    public ResponseEntity<ClockResourceWithChannel> create(@Valid @RequestBody ClockRequest clockRequest, Principal principal) {
        Clock savedClock = clocksRepository.saveClock(clockRequest.getName(), clockRequest.getSchedule());
        TickChannel channel = tickChannelCreator.create(clockRequest.getName(), clockRequest.getSchedule());
        return createdClockEntity(savedClock, channel, principal);
    }

    private ResponseEntity<ClockResourceWithChannel> createdClockEntity(Clock clock, TickChannel channel, Principal principal) {
        ClockResourceWithChannel clockResource =
                new ClockResourceWithChannel(domain, clock, channel);
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
    public ClockResource findOne(@PathVariable("id") String id) {
        return createClockResourceFor(clocksRepository.findById(id).get());
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

    @PostMapping("/purge")
    @ApiOperation("Purge clocks with no active schedules")
    public ResponseEntity<Void> purge() {
        clocksPurger.purge();
        return ResponseEntity.noContent().build();
    }

}
