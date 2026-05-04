package k23cnt2.nhom4.prj4.ttcd.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "order_item_options")
@Data
public class OrderItemOption {
    @EmbeddedId
    private OrderItemOptionId id;

    @MapsId("orderItemId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @Column(name = "price_at_buy")
    private BigDecimal priceAtBuy;
}
