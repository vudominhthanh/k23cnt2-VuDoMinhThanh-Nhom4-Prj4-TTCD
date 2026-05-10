package k23cnt2.nhom4.prj4.ttcd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/customer")
public class UserController {

    @GetMapping("/dashboard")
    public String customerDashboard() {
        return "/dashboard.html";
    }

    @GetMapping("/profile")
    public String cus_profile() {
        return "/profile.html";
    }

    @GetMapping("/address")
    public String cus_address() {
        return "/address.html";
    }

    @GetMapping("/cart")
    public String cus_cart() {
        return "/cart.html";
    }
    @GetMapping("/order")
    public String cus_order() {
        return "/order.html";
    }
}
