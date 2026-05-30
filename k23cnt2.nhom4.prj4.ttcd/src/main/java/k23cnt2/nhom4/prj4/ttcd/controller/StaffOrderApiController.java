package k23cnt2.nhom4.prj4.ttcd.controller;

import k23cnt2.nhom4.prj4.ttcd.dto.OrderResponseDTO;
import k23cnt2.nhom4.prj4.ttcd.entity.ENUMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/staff/orders")
@CrossOrigin(origins = "*")
public class StaffOrderApiController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderDetail(@PathVariable Long id) {
        OrderResponseDTO order = new OrderResponseDTO();
        order.setId(id);
        order.setOrderCode("DH" + id);
        order.setOrderStatus(ENUMS.OrderStatus.PENDING);
        order.setFinalAmount(new java.math.BigDecimal("1800.00"));

        OrderResponseDTO.OrderItemDTO item1 = new OrderResponseDTO.OrderItemDTO();
        item1.setProductName("Matcha Latte");
        item1.setQuantity(2);
        item1.setVariantName("Size L");
        item1.setPriceAtBuy(new java.math.BigDecimal("900.00"));

        order.setItems(java.util.List.of(item1));
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String statusStr = request.get("status");
        ENUMS.OrderStatus newStatus = ENUMS.OrderStatus.valueOf(statusStr.toUpperCase());

        // TODO: Viết logic cập nhật trạng thái vào cột `n4_orderStatus` trong table `n4_order`
        // Gợi ý: Ghi thêm log lịch sử vào table `n4_orderstatushistory` tại đây

        // Tạo payload trả về cho WebSocket
        OrderResponseDTO updateNotification = new OrderResponseDTO();
        updateNotification.setId(id);
        updateNotification.setOrderCode("DH" + id);
        updateNotification.setOrderStatus(newStatus);
        updateNotification.setFinalAmount(new java.math.BigDecimal("1800.00"));

        // Phát thông báo thời gian thực đến toàn bộ máy nhân viên đang bật
        simpMessagingTemplate.convertAndSend("/topic/orders", updateNotification);

        return ResponseEntity.ok(Map.of("success", true, "message", "Đã cập nhật trạng thái đơn thành: " + newStatus));
    }
}
