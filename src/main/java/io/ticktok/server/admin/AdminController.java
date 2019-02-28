package io.ticktok.server.admin;

import io.ticktok.server.Application;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

import static java.lang.Thread.sleep;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private Environment env;

    @GetMapping("/restart")
    public void restart(@RequestParam String profiles) throws InterruptedException {
        if(!Arrays.equals(currentProfiles(), toSortedArray(profiles))) {
            log.info("Changing active profile to: {}", profiles);
            Application.restart("--spring.profiles.active=" + profiles);
        }
    }

    private String[] currentProfiles() {
        String[] p = env.getActiveProfiles().length == 0 ? env.getDefaultProfiles() : env.getActiveProfiles();
        Arrays.sort(p);
        return p;
    }

    private String[] toSortedArray(String profiles) {
        String[] p = profiles.split(",");
        Arrays.sort(p);
        return p;
    }
}
