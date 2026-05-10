package k23cnt2.nhom4.prj4.ttcd.service;

import k23cnt2.nhom4.prj4.ttcd.config.JwtUtils;
import k23cnt2.nhom4.prj4.ttcd.dto.LoginRequest;
import k23cnt2.nhom4.prj4.ttcd.dto.RegisterRequest;
import k23cnt2.nhom4.prj4.ttcd.entity.ENUMS;
import k23cnt2.nhom4.prj4.ttcd.entity.User;
import k23cnt2.nhom4.prj4.ttcd.dto.AuthResponse;
import k23cnt2.nhom4.prj4.ttcd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest registerRequest) {

        if(userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email đã tồn tại !");
        }
        if(userRepository.existsByPhone(registerRequest.getPhone())) {
            throw new RuntimeException("Số điện thoại đã tồn tại !");
        }

        User newUser = new User();
        newUser.setEmail(registerRequest.getEmail());
        newUser.setFullName(registerRequest.getFullName());
        newUser.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setPhone(registerRequest.getPhone());

        newUser.setRole(ENUMS.UserRole.CUSTOMER);
        newUser.setIsActive(true);

        userRepository.save(newUser);

        return new AuthResponse(null, "Đăng kí thành công !");
    }

    public AuthResponse login(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtils.generateToken(authentication);

        return new AuthResponse(token, "Đăng nhập thành công !");
    }
}