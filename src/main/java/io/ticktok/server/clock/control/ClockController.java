package io.ticktok.server.clock.control;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.actions.ClockActionFactory;
import io.ticktok.server.clock.repository.ClocksRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = {"clocks"})
@RestController
@RequestMapping("/api/v1/clocks/{id}")
public class ClockController {

    private final ClockActionFactory clockActionFactory;
    private final ClockResourceFactory clockResourceFactory;
    private final ClocksRepository clocksRepository;

    public ClockController(ClockActionFactory clockActionFactory, ClocksRepository clocksRepository) {
        this.clockActionFactory = clockActionFactory;
        this.clockResourceFactory = new ClockResourceFactory(clockActionFactory);
        this.clocksRepository = clocksRepository;
    }

    @PutMapping("/{action}")
    @ApiOperation(value = "Run an action on a specific clock")
    public ResponseEntity<ClockResource> clockAction(
            Model model,
            @ApiParam(required = true, allowableValues = "pause,resume,tick") @PathVariable String action) {
        final Clock clock = clockFrom(model);
        log.info("CLOCK-ACTION: {} on clock: {}", action, clock.getId());
        clockActionFactory.create(action).run(clock);
        return ResponseEntity.ok(getClockById(clock.getId()));
    }

    private ClockResource getClockById(String id) {
        return clockResourceFactory.create(clocksRepository.findById(id).get());
    }

    private Clock clockFrom(Model model) {
        return (Clock) model.asMap().get("clock");
    }

    @GetMapping()
    @ApiOperation("Retrieve a specific clock")
    public ClockResource findOne(Model model) {
        return clockResourceFactory.create(clockFrom(model));
    }
}
