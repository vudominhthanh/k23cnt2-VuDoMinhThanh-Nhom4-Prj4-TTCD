package k23cnt2.nhom4.prj4.ttcd.repository;

import ch.qos.logback.core.status.Status;
import k23cnt2.nhom4.prj4.ttcd.entity.ENUMS;
import k23cnt2.nhom4.prj4.ttcd.entity.Order;
import k23cnt2.nhom4.prj4.ttcd.entity.OrderStatusHistory;
import k23cnt2.nhom4.prj4.ttcd.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    Optional<Order> findById(Long id);

    List<Order> findTop5ByUserOrderByCreatedAtDesc(User user);

    List<Order> findByUserOrderByCreatedAtDesc(User user);

    Optional<Order> findByOrderCode(String orderCode);

    long countByUserAndOrderStatusIn(User user, List<ENUMS.OrderStatus> statuses);

    List<Order> findByOrderStatusInOrderByCreatedAtDesc(List<ENUMS.OrderStatus> statuses);

}
