package k23cnt2.nhom4.prj4.ttcd.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_pay_order_id", columnList = "order_id"),
        @Index(name = "idx_pay_status", columnList = "payment_status"),
        @Index(name = "idx_trans_id", columnList = "transaction_id")
})
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private ENUMS.PaymentMethod paymentMethod = ENUMS.PaymentMethod.CASH;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private ENUMS.PaymentStatus paymentStatus = ENUMS.PaymentStatus.PENDING;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}