package k23cnt2.nhom4.prj4.ttcd.controller;

import k23cnt2.nhom4.prj4.ttcd.dto.DashboardData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
public class AdminController {

    @GetMapping("/admin-dashboard")
    public String AdminDashboard(Model model) {
        DashboardData.Stats stats = new DashboardData.Stats(125, 15500000.0, 42);

        List<DashboardData.RecentOrder> recentOrders = Arrays.asList(
                new DashboardData.RecentOrder("ORD-1001", "Vũ Đỗ Minh Thành", "Đang pha chế", 155000.0),
                new DashboardData.RecentOrder("ORD-1002", "Nguyễn Văn A", "Chờ xác nhận", 85000.0),
                new DashboardData.RecentOrder("ORD-1003", "Trần Thị B", "Hoàn thành", 210000.0)
        );

        // Đẩy dữ liệu sang Thymeleaf
        model.addAttribute("adminName", "Admin Chanh Leo");
        model.addAttribute("stats", stats);
        model.addAttribute("recentOrders", recentOrders);                                                      

        return "/Admin/admin-dashboard.html";
    }
}
