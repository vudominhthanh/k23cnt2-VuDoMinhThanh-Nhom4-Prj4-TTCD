package k23cnt2.nhom4.prj4.ttcd.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(indexes = {
        @Index(name = "idx_product_slug", columnList = "slug"),
        @Index(name = "idx_prod_cat_id", columnList = "category_id")
})
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn()
    private Category category;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, nullable = false, length = 100)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @Column(nullable = false)
    private BigDecimal basePrice;

    @Column()
    private Boolean isActive = true;

    @ManyToMany
    @JoinTable(
            name = "product_options",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private Set<ProductOption> options = new HashSet<>();

    @OneToMany(mappedBy = "product")
    private Set<ProductVariant> productVariants = new HashSet<>();

    @OneToMany(mappedBy = "product")
    private Set<Review> reviews = new HashSet<>();

}