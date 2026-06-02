package k23cnt2.nhom4.prj4.ttcd.controller;

import k23cnt2.nhom4.prj4.ttcd.dto.CheckoutRequest;
import k23cnt2.nhom4.prj4.ttcd.dto.OrderResponseDTO;
import k23cnt2.nhom4.prj4.ttcd.entity.Order;
import k23cnt2.nhom4.prj4.ttcd.entity.Payment;
import k23cnt2.nhom4.prj4.ttcd.repository.OrderRepository;
import k23cnt2.nhom4.prj4.ttcd.repository.PaymentRepository;
import k23cnt2.nhom4.prj4.ttcd.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderApiController {

    @Autowired
    OrderService orderService;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/checkout")
    public ResponseEntity<?> createOrder(@RequestBody CheckoutRequest request, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Yêu cầu đăng nhập!");
        }
        try {
            Order order = orderService.checkout(authentication.getName(), request);

            OrderResponseDTO notification = new OrderResponseDTO();
            notification.setId(order.getId());
            notification.setOrderCode(order.getOrderCode());
            notification.setOrderStatus(order.getOrderStatus());
            notification.setFinalAmount(order.getFinalAmount());
            simpMessagingTemplate.convertAndSend("/topic/orders", notification);

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.getId());
            response.put("orderCode", order.getOrderCode());
            response.put("finalAmount", order.getFinalAmount());

            Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
            if (payment != null) {
                response.put("transactionId", payment.getTransactionId());
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String email = authentication.getName();
        List<OrderResponseDTO> orders = orderService.getMyOrders(email);

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderCode}")
    public ResponseEntity<OrderResponseDTO> getOrderDetails(@PathVariable String orderCode) {
        return ResponseEntity.ok(orderService.getOrderDetail(orderCode));
    }

    @PutMapping("/{orderCode}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable("orderCode") String orderCode,
            @RequestParam("newStatus") String newStatus,
            Authentication authentication) {

        try {
            String username = authentication.getName();

            orderService.updateOrderStatus(orderCode, newStatus);

            return ResponseEntity.ok().body("Cập nhật trạng thái đơn hàng thành công!");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi hệ thống: " + e.getMessage());
        }
    }
}
