package k23cnt2.nhom4.prj4.ttcd.controller;

import k23cnt2.nhom4.prj4.ttcd.dto.OrderResponseDTO;
import k23cnt2.nhom4.prj4.ttcd.entity.ENUMS;
import k23cnt2.nhom4.prj4.ttcd.entity.Order;
import k23cnt2.nhom4.prj4.ttcd.repository.OrderRepository;
import k23cnt2.nhom4.prj4.ttcd.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/staff/orders")
@CrossOrigin(origins = "*")
public class StaffOrderApiController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(@PathVariable Long id) {
        try {
            // Gọi Service để lấy dữ liệu thật từ Database
            OrderResponseDTO order = orderService.getOrderDetailById(id);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String statusStr = request.get("status");

            // Gọi Service để Lưu trạng thái mới xuống Database (n4_order)
            Order updatedOrder = orderService.updateOrderStatusById(id, statusStr);

            // Bắn tín hiệu WebSocket cho toàn bộ các máy POS khác cập nhật giao diện
            OrderResponseDTO updateNotification = new OrderResponseDTO();
            updateNotification.setId(updatedOrder.getId());
            updateNotification.setOrderCode(updatedOrder.getOrderCode());
            updateNotification.setOrderStatus(updatedOrder.getOrderStatus());
            updateNotification.setFinalAmount(updatedOrder.getFinalAmount());
            simpMessagingTemplate.convertAndSend("/topic/orders", updateNotification);

            return ResponseEntity.ok(Map.of("success", true, "message", "Đã cập nhật trạng thái đơn thành: " + statusStr));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi cập nhật: " + e.getMessage());
        }
    }

    @PostMapping("/create-at-counter")
    public ResponseEntity<?> createOrderAtCounter(
            @RequestBody Map<String, Object> orderPayload,
            Authentication authentication) {
        try {
            String staffUsername = authentication.getName();

            Order newOrder = orderService.createPOSOrder(staffUsername, orderPayload);

            OrderResponseDTO notification = new OrderResponseDTO();
            notification.setId(newOrder.getId());
            notification.setOrderCode(newOrder.getOrderCode());
            notification.setOrderStatus(newOrder.getOrderStatus());
            notification.setFinalAmount(newOrder.getFinalAmount());
            simpMessagingTemplate.convertAndSend("/topic/orders", notification);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Tạo hóa đơn tại quầy thành công!"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi tạo đơn: " + e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllActiveOrders() {
        try {
            return ResponseEntity.ok(orderService.getActiveOrdersForStaff());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
