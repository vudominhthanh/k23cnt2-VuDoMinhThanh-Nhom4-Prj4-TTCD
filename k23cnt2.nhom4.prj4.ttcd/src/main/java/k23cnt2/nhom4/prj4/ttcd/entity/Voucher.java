package k23cnt2.nhom4.prj4.ttcd.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(indexes = {
        @Index(name = "idx_voucher_code", columnList = "code")
})
@Data
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ENUMS.DiscountType discountType;

    @Column(nullable = false)
    private BigDecimal discountValue;

    @Column()
    private BigDecimal maxDiscountAmount;

    @Column()
    private BigDecimal minOrderValue = BigDecimal.ZERO;

    @Column()
    private Integer usageLimit;

    @Column()
    private Integer usedCount = 0;

    @Column()
    private LocalDateTime validFrom;

    @Column()
    private LocalDateTime validTo;

    @Column()
    private Boolean isActive = true;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
