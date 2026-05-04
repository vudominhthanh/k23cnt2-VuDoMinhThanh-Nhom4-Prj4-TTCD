package k23cnt2.nhom4.prj4.ttcd.service;

import k23cnt2.nhom4.prj4.ttcd.dto.LoginRequest;
import k23cnt2.nhom4.prj4.ttcd.entity.ENUMS;
import k23cnt2.nhom4.prj4.ttcd.entity.User;
import k23cnt2.nhom4.prj4.ttcd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    public User register(User user) {
        user.setEmail(user.getEmail());
        user.setPhone(user.getPhone());
        user.setFullName(user.getFullName());
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setRole(ENUMS.UserRole.CUSTOMER);
        return userRepository.save(user);
    }

    public User login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        if (authentication.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        }

        throw new UsernameNotFoundException("Invalid username or password");
    }

}
