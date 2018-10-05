package io.ticktok.server.dashboard;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @Value("${http.domain}")
    private String domain;

    @GetMapping(value = "/")
    public String index(Model model) {
        model.addAttribute("SELF_DOMAIN", domain);
        model.addAttribute("eli", "hello");
        return "index";
    }

    @GetMapping(value = "/test")
    public String test(Model model) {
        model.addAttribute("SELF_DOMAIN", domain);
        model.addAttribute("eli", "hello");
        return "test";
    }
}
