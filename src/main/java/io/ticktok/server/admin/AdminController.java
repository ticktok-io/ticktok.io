package io.ticktok.server.admin;

import io.ticktok.server.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private Environment env;

    @GetMapping("/restart")
    public void restart(@RequestParam String profiles) throws InterruptedException {
        if(!Arrays.equals(currentProfiles(), toSortedArray(profiles))) {
            Application.restart("--spring.profiles.active=" + profiles);
        }
    }

    private String[] currentProfiles() {
        String[] p = env.getDefaultProfiles();
        Arrays.sort(p);
        return p;
    }

    private String[] toSortedArray(String profiles) {
        String[] p = profiles.split(",");
        Arrays.sort(p);
        return p;
    }
}
