package k23cnt2.nhom4.prj4.ttcd.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(indexes = {
        @Index(name = "idx_pay_order_id", columnList = "order_id"),
        @Index(name = "idx_pay_status", columnList = "paymentStatus"),
        @Index(name = "idx_trans_id", columnList = "transactionId")
})
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column()
    private ENUMS.PaymentMethod paymentMethod = ENUMS.PaymentMethod.CASH;

    @Enumerated(EnumType.STRING)
    @Column()
    private ENUMS.PaymentStatus paymentStatus = ENUMS.PaymentStatus.PENDING;

    @Column(length = 100)
    private String transactionId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column()
    private LocalDateTime paidAt;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;
}