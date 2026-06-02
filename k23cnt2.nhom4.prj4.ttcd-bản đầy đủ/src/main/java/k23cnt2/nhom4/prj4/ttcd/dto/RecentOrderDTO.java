package k23cnt2.nhom4.prj4.ttcd.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class RecentOrderDTO {
    private String orderId;
    private LocalDateTime orderCreatedAt;
    private BigDecimal totalAmount;
    private String status;

}