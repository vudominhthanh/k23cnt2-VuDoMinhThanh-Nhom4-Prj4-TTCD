package k23cnt2.nhom4.prj4.ttcd.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(indexes = {
        @Index(name = "idx_var_product_id", columnList = "product_id")
})
@Getter
@Setter
@EqualsAndHashCode(exclude = "product")
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