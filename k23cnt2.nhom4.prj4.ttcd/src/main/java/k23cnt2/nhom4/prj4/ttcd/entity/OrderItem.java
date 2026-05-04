package k23cnt2.nhom4.prj4.ttcd.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_name", length = 100)
    private String productName;

    @Column(name = "variant_name", length = 20)
    private String variantName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "price_at_buy", nullable = false)
    private BigDecimal priceAtBuy;
}