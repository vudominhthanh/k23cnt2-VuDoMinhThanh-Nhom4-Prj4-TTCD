package k23cnt2.nhom4.prj4.ttcd.service;

import k23cnt2.nhom4.prj4.ttcd.dto.AdminDashboardData;
import k23cnt2.nhom4.prj4.ttcd.entity.ENUMS;
import k23cnt2.nhom4.prj4.ttcd.entity.Order;
import k23cnt2.nhom4.prj4.ttcd.repository.AdminOrderRepository;
import k23cnt2.nhom4.prj4.ttcd.repository.AdminProductRepository;
import k23cnt2.nhom4.prj4.ttcd.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminDashboardService {

    @Autowired
    private AdminUserRepository userRepository;

    @Autowired
    private AdminProductRepository productRepository;

    @Autowired
    private AdminOrderRepository orderRepository;

    public List<Order> getPendingOrders() {

        return orderRepository.findByOrderStatus(
                ENUMS.OrderStatus.PENDING
        );
    }

    public AdminDashboardData getStats() {

        AdminDashboardData stats =
                new AdminDashboardData();

        stats.setTotalOrders(
                orderRepository.count()
        );

        stats.setTotalCustomers(
                userRepository.countCustomers()
        );

        stats.setTotalProducts(
                productRepository.count()
        );

        stats.setTotalRevenue(
                orderRepository.getTotalRevenue()
                        .doubleValue()
        );

        return stats;
    }
}