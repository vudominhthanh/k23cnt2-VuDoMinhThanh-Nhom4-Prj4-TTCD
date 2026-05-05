package k23cnt2.nhom4.prj4.ttcd.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(indexes = {
        @Index(name = "idx_cart_item_cart_id", columnList = "cart_id")
})
@Data
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ProductVariant variant;

    private Integer quantity = 1;

    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "n4_cart_item_id"),
            inverseJoinColumns = @JoinColumn(name = "n4_option_id")
    )
    private Set<ProductOption> options = new HashSet<>();
}