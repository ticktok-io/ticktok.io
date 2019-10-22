package io.ticktok.server.clock.control;

import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.clock.repository.RepositoryClocksFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import static io.ticktok.server.clock.control.HttpRequestUtil.pathParam;

@ControllerAdvice(assignableTypes={ClockController.class})
public class IdToClockControllerAdvice {

    @Autowired
    private ClocksRepository clocksRepository;



    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("clock", new RepositoryClocksFinder(clocksRepository).findById(pathParam("id")));
    }
}
