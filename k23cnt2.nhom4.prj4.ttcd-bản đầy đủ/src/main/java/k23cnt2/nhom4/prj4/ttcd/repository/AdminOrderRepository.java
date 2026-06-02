package k23cnt2.nhom4.prj4.ttcd.repository;

import k23cnt2.nhom4.prj4.ttcd.entity.ENUMS;
import k23cnt2.nhom4.prj4.ttcd.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface AdminOrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByOrderStatus(ENUMS.OrderStatus orderStatus);

    @Query("""
        SELECT COALESCE(SUM(o.finalAmount), 0)
        FROM Order o
        """)
    BigDecimal getTotalRevenue();
}