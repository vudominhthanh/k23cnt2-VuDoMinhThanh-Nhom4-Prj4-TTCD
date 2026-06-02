package k23cnt2.nhom4.prj4.ttcd.service;

import k23cnt2.nhom4.prj4.ttcd.entity.ENUMS;
import k23cnt2.nhom4.prj4.ttcd.entity.Order;
import k23cnt2.nhom4.prj4.ttcd.repository.AdminOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminOrderService {

    @Autowired
    private AdminOrderRepository orderRepository;

    // lấy tất cả đơn hàng
    public List<Order> getAllOrders() {

        return orderRepository.findAll();
    }

    // lấy theo id
    public Order getById(Long id){

        return orderRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Không tìm thấy đơn hàng")
                );
    }

    // cập nhật trạng thái
    public Order updateStatus(
            Long id,
            ENUMS.OrderStatus status
    ) {

        Order order = getById(id);

        order.setOrderStatus(status);

        return orderRepository.save(order);
    }
}