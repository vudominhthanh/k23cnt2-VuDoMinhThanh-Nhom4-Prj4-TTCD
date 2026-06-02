package k23cnt2.nhom4.prj4.ttcd.controller;

import k23cnt2.nhom4.prj4.ttcd.entity.ENUMS;
import k23cnt2.nhom4.prj4.ttcd.entity.Order;
import k23cnt2.nhom4.prj4.ttcd.service.AdminOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class AdminOrderController {

    @Autowired
    private AdminOrderService orderService;

    @GetMapping("/admin/orders")
    public String listOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();

        model.addAttribute("orders", orders);

        model.addAttribute("statuses", ENUMS.OrderStatus.values());

        return "Admin/admin-orders";
    }

    @GetMapping("/admin/orders/{id}")
    public String orderDetail(@PathVariable Long id, Model model){
        Order order = orderService.getById(id);

        if (order.getUser() != null) {
            order.getUser().getFullName();
        }

        model.addAttribute("order", order);
        return "Admin/order-detail";
    }
}