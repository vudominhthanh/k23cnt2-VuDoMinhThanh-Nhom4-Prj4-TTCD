package k23cnt2.nhom4.prj4.ttcd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/customer")
public class UserController {
    @GetMapping("/dashboard")
    public String dashboard() {
        return "/User/dashboard.html";
    }
    @GetMapping("/cart")
    public String cart() {
        return "/User/cart.html";
    }
    @GetMapping("/orders")
    public String orders() {
        return "/User/orders.html";
    }
    @GetMapping("/settings")
    public String settings() {
        return "/User/settings.html";
    }
}
