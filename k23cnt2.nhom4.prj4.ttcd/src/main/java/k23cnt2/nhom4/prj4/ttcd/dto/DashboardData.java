package k23cnt2.nhom4.prj4.ttcd.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DashboardData {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Stats {
        private int totalOrders;
        private double totalRevenue;
        private int newCustomers;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecentOrder {
        private String orderId;
        private String customerName;
        private String status;
        private double amount;
    }
}