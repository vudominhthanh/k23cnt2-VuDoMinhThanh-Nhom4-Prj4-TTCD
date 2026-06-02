package k23cnt2.nhom4.prj4.ttcd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "/User/home.html";
    }

    @GetMapping("/about")
    public String about() {
        return "/about.html";
    }
}
