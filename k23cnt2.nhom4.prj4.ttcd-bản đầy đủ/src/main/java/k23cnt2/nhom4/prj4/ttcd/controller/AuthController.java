package k23cnt2.nhom4.prj4.ttcd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    @GetMapping("/auth")
    public String auth() {
        return "auth.html";
    }
}
