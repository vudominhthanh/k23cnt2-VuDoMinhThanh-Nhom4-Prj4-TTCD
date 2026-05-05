package k23cnt2.nhom4.prj4.ttcd.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(indexes = {
        @Index(name = "idx_var_product_id", columnList = "product_id")
})
@Data
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Product product;

    @Column(length = 20)
    private String sizeName;

    @Column()
    private BigDecimal extraPrice = BigDecimal.ZERO;
}