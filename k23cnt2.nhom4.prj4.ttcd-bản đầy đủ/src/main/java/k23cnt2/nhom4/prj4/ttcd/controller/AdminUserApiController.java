package k23cnt2.nhom4.prj4.ttcd.controller;

import k23cnt2.nhom4.prj4.ttcd.entity.User;
import k23cnt2.nhom4.prj4.ttcd.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserApiController {

    @Autowired
    private AdminUserService userService;

    // GET ALL
    @GetMapping
    public List<User> getAllUsers() {

        return userService.getAllUsers();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {

        return userService.getUserById(id);
    }

    // CREATE
    @PostMapping
    public User createUser(@RequestBody User user) {

        return userService.createUser(user);
    }

    // UPDATE
    @PutMapping("/{id}")
    public User updateUser(
            @PathVariable Integer id,
            @RequestBody User user
    ) {

        return userService.updateUser(id, user);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Integer id) {

        userService.deleteUser(id);

        return "Xóa user thành công!";
    }
}