package k23cnt2.nhom4.prj4.ttcd.controller;

import k23cnt2.nhom4.prj4.ttcd.config.JwtUtils;
import k23cnt2.nhom4.prj4.ttcd.dto.AuthResponse;
import k23cnt2.nhom4.prj4.ttcd.dto.LoginRequest;
import k23cnt2.nhom4.prj4.ttcd.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import k23cnt2.nhom4.prj4.ttcd.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            AuthResponse authResponse = authService.register(registerRequest);
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new AuthResponse(null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new AuthResponse(null, "Có lỗi xảy ra trong quá trình đăng ký!"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse(null, "Email hoặc mật khẩu không chính xác!"));
        }
    }
}
