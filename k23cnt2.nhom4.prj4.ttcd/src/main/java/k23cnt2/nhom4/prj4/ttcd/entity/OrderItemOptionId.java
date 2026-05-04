package k23cnt2.nhom4.prj4.ttcd.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class OrderItemOptionId implements Serializable {
    private Long orderItemId;
    private String optionName;
}