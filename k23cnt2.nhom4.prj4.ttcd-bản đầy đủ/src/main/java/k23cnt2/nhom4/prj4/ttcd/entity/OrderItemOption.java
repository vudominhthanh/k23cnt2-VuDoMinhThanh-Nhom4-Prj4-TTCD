package k23cnt2.nhom4.prj4.ttcd.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Data
public class OrderItemOption {
    @EmbeddedId
    private OrderItemOptionId id;

    @MapsId("orderItemId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn()
    private OrderItem orderItem;

    @Column()
    private BigDecimal priceAtBuy;
}
