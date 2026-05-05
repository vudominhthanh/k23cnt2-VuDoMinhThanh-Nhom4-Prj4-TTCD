package k23cnt2.nhom4.prj4.ttcd.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class ProductOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String optionName;

    @Column(length = 50)
    private String optionType; // TOPPING, SUGAR, ICE

    @Column()
    private BigDecimal additionalPrice = BigDecimal.ZERO;
}