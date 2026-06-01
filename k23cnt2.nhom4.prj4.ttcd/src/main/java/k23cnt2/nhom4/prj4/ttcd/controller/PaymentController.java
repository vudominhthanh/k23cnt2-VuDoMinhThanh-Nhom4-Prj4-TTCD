package k23cnt2.nhom4.prj4.ttcd.controller;

import k23cnt2.nhom4.prj4.ttcd.entity.ENUMS;
import k23cnt2.nhom4.prj4.ttcd.entity.Order;
import k23cnt2.nhom4.prj4.ttcd.entity.Payment;
import k23cnt2.nhom4.prj4.ttcd.repository.OrderRepository;
import k23cnt2.nhom4.prj4.ttcd.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @PostMapping("/mock-callback")
    public ResponseEntity<?> handleMockCallback(@RequestParam String transactionId, @RequestParam boolean isSuccess) {

        Optional<Payment> paymentOptional =  paymentRepository.findByTransactionId(transactionId);

        if (paymentOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("không tìm thấy giao dịch : " + transactionId);
        }

        Payment payment = paymentOptional.get();
        Order order = payment.getOrder();

        if (order == null) {
            return ResponseEntity.badRequest().body("Giao dịch này không được gắn với đơn hàng nào!");
        }

        if (isSuccess) {
            order.setOrderStatus(ENUMS.OrderStatus.PENDING);

        } else {
            order.setOrderStatus(ENUMS.OrderStatus.PENDING);
        }

        orderRepository.save(order);

        Map<String,Object> response = new HashMap<>();
        response.put("message", "Đã cập nhật trạng thái đơn hàng");
        response.put("transactionId", transactionId);
        response.put("newOrderStatus", order.getOrderStatus());

        return ResponseEntity.ok(response);
    }

}
