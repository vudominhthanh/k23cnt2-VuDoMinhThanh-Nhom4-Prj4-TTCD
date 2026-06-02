package k23cnt2.nhom4.prj4.ttcd.controller;

import k23cnt2.nhom4.prj4.ttcd.dto.OrderResponseDTO;
import k23cnt2.nhom4.prj4.ttcd.dto.PasswordUpdateDTO;
import k23cnt2.nhom4.prj4.ttcd.dto.UserProfileDTO;
import k23cnt2.nhom4.prj4.ttcd.entity.Order;
import k23cnt2.nhom4.prj4.ttcd.repository.UserRepository;
import k23cnt2.nhom4.prj4.ttcd.service.CustomerService;
import k23cnt2.nhom4.prj4.ttcd.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/customer")
public class UserApiController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/overview")
    public ResponseEntity<?> getOverview(Authentication authentication) {
        return ResponseEntity.ok(customerService.getDashboardOverview(authentication.getName()));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        UserProfileDTO profile = customerService.getProfile(authentication.getName());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile/update")
    public ResponseEntity<?> updateProfile(Authentication authentication, @RequestBody UserProfileDTO dto) {
        boolean success = customerService.updateProfile(authentication.getName(), dto);
        return success ? ResponseEntity.ok("Cập nhật thành công") : ResponseEntity.badRequest().body("Lỗi dữ liệu");
    }

    @PutMapping("/profile/password")
    public ResponseEntity<?> updatePassword(Authentication authentication, @RequestBody PasswordUpdateDTO dto) {
        boolean success = customerService.updatePassword(authentication.getName(), dto);
        return success ? ResponseEntity.ok("Đổi mật khẩu thành công") : ResponseEntity.badRequest().body("Mật khẩu cũ không chính xác");
    }

    // 2. API CẬP NHẬT TRẠNG THÁI ĐƠN HÀNG (SỬA LẠI ĐỂ BẮN WEBSOCKET PRIVATE)
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String statusStr = request.get("status");

            // Gọi Service để Lưu trạng thái mới xuống Database (n4_order)
            Order updatedOrder = orderService.updateOrderStatusById(id, statusStr);

            // TẠO GÓI THÔNG BÁO CHUNG
            OrderResponseDTO updateNotification = new OrderResponseDTO();
            updateNotification.setId(updatedOrder.getId());
            updateNotification.setOrderCode(updatedOrder.getOrderCode());
            updateNotification.setOrderStatus(updatedOrder.getOrderStatus());
            updateNotification.setFinalAmount(updatedOrder.getFinalAmount());

            // 1. Phát loa cho TOÀN BỘ NHÂN VIÊN (Máy POS khác cập nhật giao diện)
            simpMessagingTemplate.convertAndSend("/topic/orders", updateNotification);

            // 2. Gửi tin nhắn KÍN cho ĐÚNG KHÁCH HÀNG sở hữu đơn đó (Nếu có tài khoản)
            if (updatedOrder.getUser() != null) {
                String customerEmail = updatedOrder.getUser().getEmail();

                simpMessagingTemplate.convertAndSend("/topic/orders/" + customerEmail, updateNotification);
            }

            return ResponseEntity.ok(Map.of("success", true, "message", "Đã cập nhật trạng thái đơn thành: " + statusStr));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi cập nhật: " + e.getMessage());
        }
    }
}
