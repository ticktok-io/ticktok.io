package io.ticktok.server.clock.actions;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ClockActionFactory {

    private final Map<String, ClockAction> actions;

    public ClockActionFactory(List<ClockAction> actions) {
        this.actions = actions.stream().collect(
                Collectors.toMap(this::actionNameFor, x -> x));
    }

    private String actionNameFor(ClockAction action) {
        return action.getClass().getSimpleName()
                .replaceAll(ClockAction.class.getSimpleName(), "")
                .toLowerCase();
    }

    public void run(String actionName, String id) {
        validateActionExists(actionName);
        actions.get(actionName).run(id);
    }

    private void validateActionExists(String actionName) {
        if(!actions.containsKey(actionName)) {
            throw new ActionNotFoundException("Failed to find action for: " + actionName);
        }
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Action not supported")
    public static class ActionNotFoundException extends RuntimeException {
        public ActionNotFoundException(String message) {
            super(message);
        }
    }
}
