package k23cnt2.nhom4.prj4.ttcd.controller;

import k23cnt2.nhom4.prj4.ttcd.service.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private AdminDashboardService dashboardService;

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {

        model.addAttribute(
                "stats",
                dashboardService.getStats()
        );

        model.addAttribute(
                "recentOrders",
                dashboardService.getPendingOrders()
        );

        List<String> labels = List.of(
                "Tháng 01", "Tháng 02", "Tháng 03", "Tháng 04", "Tháng 05", "Tháng 06",
                "Tháng 07", "Tháng 08", "Tháng 09", "Tháng 10", "Tháng 11", "Tháng 12"
        );

        List<BigDecimal> revenue = Arrays.asList(
                new BigDecimal("12500000"),
                new BigDecimal("18200000"),
                new BigDecimal("15000000"),
                new BigDecimal("29400000"),
                new BigDecimal("21000000"),
                new BigDecimal("35600000"),
                null,
                null,
                null,
                null,
                null,
                null
        );

        model.addAttribute("chartLabels", labels);
        model.addAttribute("chartData", revenue);

        return "Admin/admin-dashboard";
    }
}