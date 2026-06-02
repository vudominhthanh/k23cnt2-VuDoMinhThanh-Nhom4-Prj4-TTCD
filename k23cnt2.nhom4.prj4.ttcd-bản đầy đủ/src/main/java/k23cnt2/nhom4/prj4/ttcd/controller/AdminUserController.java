package k23cnt2.nhom4.prj4.ttcd.controller;

import k23cnt2.nhom4.prj4.ttcd.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminUserController {

    @Autowired
    private AdminUserService userService;

    @GetMapping("/admin/users")
    public String usersPage(Model model) {

        model.addAttribute(
                "users",
                userService.getAllUsers()
        );

        return "Admin/admin-users";
    }
}