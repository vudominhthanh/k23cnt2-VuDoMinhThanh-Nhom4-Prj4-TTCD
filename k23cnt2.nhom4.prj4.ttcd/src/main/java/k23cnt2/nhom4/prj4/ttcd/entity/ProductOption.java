package k23cnt2.nhom4.prj4.ttcd.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "product_options")
@Data
public class ProductOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "option_name", nullable = false, length = 50)
    private String optionName;

    @Column(name = "option_type", length = 50)
    private String optionType; // TOPPING, SUGAR, ICE

    @Column(name = "additional_price")
    private BigDecimal additionalPrice = BigDecimal.ZERO;
}