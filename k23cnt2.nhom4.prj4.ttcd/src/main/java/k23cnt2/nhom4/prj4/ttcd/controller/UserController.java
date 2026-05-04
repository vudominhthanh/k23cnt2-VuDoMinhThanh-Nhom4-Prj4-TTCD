package k23cnt2.nhom4.prj4.ttcd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class UserController {

    @GetMapping("/")
    public String home() {
        return "/User/home.html";
    }

    @GetMapping("/customer/dashboard")
    public String customerDashboard() {
        return "/User/dashboard.html";
    }

    @GetMapping("/customer/profile")
    public String cus_profile() {
        return "/User/profile.html";
    }

    @GetMapping("/customer/address")
    public String cus_address() {
        return "/User/address.html";
    }

    @GetMapping("/customer/cart")
    public String cus_cart() {
        return "/User/cart.html";
    }
    @GetMapping("/customer/order")
    public String cus_order() {
        return "/User/order.html";
    }
}
