package k23cnt2.nhom4.prj4.ttcd.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class DashboardDTO {
    private int loyaltyPoints;
    private int cartCount;
    private int pendingOrdersCount;
    private List<RecentOrderDTO> recentOrders;

}
