package k23cnt2.nhom4.prj4.ttcd.repository;

import k23cnt2.nhom4.prj4.ttcd.entity.ENUMS;
import k23cnt2.nhom4.prj4.ttcd.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AdminUserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    // Dashboard
    long countByRole(ENUMS.UserRole role);
    @Query(value = """
    SELECT COUNT(*)
    FROM n4_user
    WHERE n4_role = 'CUSTOMER'
    """, nativeQuery = true)
    long countCustomers();
    long countByIsActive(Boolean isActive);
}