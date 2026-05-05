package k23cnt2.nhom4.prj4.ttcd.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(indexes = {
        @Index(name = "idx_order_code", columnList = "orderCode"),
        @Index(name = "idx_order_user_id", columnList = "user_id"),
        @Index(name = "idx_order_status", columnList = "orderStatus")
})
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String orderCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn()
    private Voucher voucher;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column()
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal finalAmount;

    @Enumerated(EnumType.STRING)
    @Column()
    private ENUMS.OrderStatus orderStatus = ENUMS.OrderStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;
}