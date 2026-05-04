package k23cnt2.nhom4.prj4.ttcd.controller;

import k23cnt2.nhom4.prj4.ttcd.dto.LoginRequest;
import k23cnt2.nhom4.prj4.ttcd.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import k23cnt2.nhom4.prj4.ttcd.service.AuthService;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/auth")
    public String login() {
        return "auth.html";
    }

    @PostMapping("/auth")
    public User login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
}
