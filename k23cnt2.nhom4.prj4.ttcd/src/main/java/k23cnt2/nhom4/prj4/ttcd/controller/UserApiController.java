package k23cnt2.nhom4.prj4.ttcd.controller;

import k23cnt2.nhom4.prj4.ttcd.dto.PasswordUpdateDTO;
import k23cnt2.nhom4.prj4.ttcd.dto.UserProfileDTO;
import k23cnt2.nhom4.prj4.ttcd.repository.UserRepository;
import k23cnt2.nhom4.prj4.ttcd.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
public class UserApiController {
    @Autowired
    private CustomerService customerService;

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
}
