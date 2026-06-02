package k23cnt2.nhom4.prj4.ttcd.service;

import k23cnt2.nhom4.prj4.ttcd.entity.User;
import k23cnt2.nhom4.prj4.ttcd.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminUserService {

    @Autowired
    private AdminUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    public User getUserById(Integer id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy user"));
    }

    public User createUser(User user) {

        // mã hóa password
        user.setPasswordHash(
                passwordEncoder.encode(user.getPasswordHash())
        );

        return userRepository.save(user);
    }


    public User updateUser(Integer id, User newUser) {

        User oldUser = getUserById(id);

        oldUser.setFullName(newUser.getFullName());
        oldUser.setEmail(newUser.getEmail());
        oldUser.setPhone(newUser.getPhone());
        oldUser.setRole(newUser.getRole());
        oldUser.setIsActive(newUser.getIsActive());

        // nếu admin nhập password mới
        if (newUser.getPasswordHash() != null
                && !newUser.getPasswordHash().isEmpty()) {

            oldUser.setPasswordHash(
                    passwordEncoder.encode(newUser.getPasswordHash())
            );
        }

        return userRepository.save(oldUser);
    }

    public void deleteUser(Integer id) {

        userRepository.deleteById(id);
    }
}