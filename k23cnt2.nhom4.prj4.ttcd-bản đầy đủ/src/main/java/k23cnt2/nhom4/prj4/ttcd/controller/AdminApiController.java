package k23cnt2.nhom4.prj4.ttcd.controller;

import k23cnt2.nhom4.prj4.ttcd.entity.ENUMS;
import k23cnt2.nhom4.prj4.ttcd.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    @Autowired
    private AdminUserRepository userRepository;

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {

        Map<String, Object> data = new HashMap<>();

        // tổng user
        data.put("totalUsers", userRepository.count());

        // số customer
        data.put("totalCustomers",
                userRepository.countByRole(ENUMS.UserRole.CUSTOMER));

        // số active
        data.put("activeUsers",
                userRepository.countByIsActive(true));

        // số inactive
        data.put("inactiveUsers",
                userRepository.countByIsActive(false));

        return data;
    }
}