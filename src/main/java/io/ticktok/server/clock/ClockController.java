package io.ticktok.server.clock;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.ticktok.server.clock.actions.ClockActionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = {"clocks"})
@RestController
@RequestMapping("/api/v1/clocks/{id}")
public class ClockController {

    private final ClockActionFactory clockActionFactory;

    public ClockController(ClockActionFactory clockActionFactory) {
        this.clockActionFactory = clockActionFactory;
    }

    @PutMapping("/{action}")
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
