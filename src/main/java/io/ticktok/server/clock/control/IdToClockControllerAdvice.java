package io.ticktok.server.clock.control;


import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.repository.ClocksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;

import static io.ticktok.server.clock.control.HttpRequestUtil.pathParam;

@ControllerAdvice(assignableTypes={ClockController.class})
public class IdToClockControllerAdvice {

    @Autowired
    private ClocksRepository clocksRepository;

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("clock", clockById(pathParam("id")));
    }

    public Clock clockById(String id) {
        return clocksRepository.findById(id).orElseThrow(
                () -> new ClockNotFoundException("Failed to find clock with id: " + id));
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Clock not found")
    public static class ClockNotFoundException extends RuntimeException {
        public ClockNotFoundException(String message) {
            super(message);
        }
    }
}
