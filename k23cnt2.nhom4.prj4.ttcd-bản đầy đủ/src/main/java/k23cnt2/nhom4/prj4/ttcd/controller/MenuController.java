package k23cnt2.nhom4.prj4.ttcd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {
    @GetMapping("/menu")
    public String menu() {
        return "/User/menu.html";
    }
}
