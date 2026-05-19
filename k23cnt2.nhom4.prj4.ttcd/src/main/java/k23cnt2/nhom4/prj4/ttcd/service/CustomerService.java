package k23cnt2.nhom4.prj4.ttcd.service;

import k23cnt2.nhom4.prj4.ttcd.dto.DashboardDTO;
import k23cnt2.nhom4.prj4.ttcd.dto.PasswordUpdateDTO;
import k23cnt2.nhom4.prj4.ttcd.dto.RecentOrderDTO;
import k23cnt2.nhom4.prj4.ttcd.dto.UserProfileDTO;
import k23cnt2.nhom4.prj4.ttcd.entity.ENUMS;
import k23cnt2.nhom4.prj4.ttcd.entity.Order;
import k23cnt2.nhom4.prj4.ttcd.entity.User;
import k23cnt2.nhom4.prj4.ttcd.repository.CartItemRepository;
import k23cnt2.nhom4.prj4.ttcd.repository.CartRepository;
import k23cnt2.nhom4.prj4.ttcd.repository.OrderRepository;
import k23cnt2.nhom4.prj4.ttcd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    public DashboardDTO getDashboardOverview(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

        DashboardDTO dto = new DashboardDTO();

        dto.setLoyaltyPoints(100);

        int totalCartItems = cartItemRepository.countTotalItemsByUser(user);
        dto.setCartCount(totalCartItems);

        List<ENUMS.OrderStatus> pendingStatuses = Arrays.asList(
                ENUMS.OrderStatus.PENDING,
                ENUMS.OrderStatus.CONFIRMED,
                ENUMS.OrderStatus.DELIVERING,
                ENUMS.OrderStatus.COMPLETED,
                ENUMS.OrderStatus.CANCELLED
        );
        long pendingCount = orderRepository.countByUserAndOrderStatusIn(user, pendingStatuses);
        dto.setPendingOrdersCount((int) pendingCount);

        List<Order> recentOrders = orderRepository.findTop5ByUserOrderByCreatedAtDesc(user);
        List<RecentOrderDTO> recentOrderDTOs = recentOrders.stream().map(order -> {
            RecentOrderDTO recentOrderDTO = new RecentOrderDTO();
            recentOrderDTO.setOrderId("#DH" + order.getId());
            recentOrderDTO.setOrderCreatedAt(order.getCreatedAt());
            recentOrderDTO.setTotalAmount(order.getTotalAmount());
            recentOrderDTO.setStatus(translateStatusToVietnamese(order.getOrderStatus()));
            return recentOrderDTO;
        }).collect(Collectors.toList());

        dto.setRecentOrders(recentOrderDTOs);
        return dto;
    }

    private String translateStatusToVietnamese(ENUMS.OrderStatus status) {
        if (status == null) return "Chưa rõ";
        switch (status) {
            case PENDING:    return "Chờ xác nhận";
            case CONFIRMED:  return "Đang pha chế";
            case DELIVERING: return "Đang giao hàng";
            case COMPLETED:  return "Hoàn thành";
            case CANCELLED:  return "Đã hủy đơn";
            default:         return status.name();
        }
    }

    public UserProfileDTO getProfile (String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại !"));

        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setFullName(user.getFullName());
        userProfileDTO.setEmail(user.getEmail());
        userProfileDTO.setPhone(user.getPhone());
        return userProfileDTO;
    }

    public boolean updateProfile(String email, UserProfileDTO userProfileDTO) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) return false;

        user.setFullName(userProfileDTO.getFullName());
        user.setPhone(userProfileDTO.getPhone());
        userRepository.save(user);
        return true;
    }

    public boolean updatePassword(String email, PasswordUpdateDTO passwordUpdateDTO) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return false;

        if (!passwordEncoder.matches(passwordUpdateDTO.getOldPassword(), user.getPasswordHash())) {
            return false;
        }

        user.setPasswordHash(passwordEncoder.encode(passwordUpdateDTO.getNewPassword()));
        userRepository.save(user);
        return true;
    }
}
