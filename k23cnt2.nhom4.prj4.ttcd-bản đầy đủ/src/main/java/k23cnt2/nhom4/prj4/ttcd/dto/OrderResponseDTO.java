package k23cnt2.nhom4.prj4.ttcd.dto;

import k23cnt2.nhom4.prj4.ttcd.entity.ENUMS;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

public class OrderResponseDTO {
    private Long id;
    private String orderCode;
    private ENUMS.OrderStatus orderStatus;
    private BigDecimal finalAmount;
    private List<OrderItemDTO> items;

    // Getters và Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }
    public ENUMS.OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(ENUMS.OrderStatus orderStatus) { this.orderStatus = orderStatus; }
    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }
    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }

    public static class OrderItemDTO {
        private String productName;
        private Integer quantity;
        private String variantName;
        private String note;
        private BigDecimal priceAtBuy;

        // Getters và Setters
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public String getVariantName() { return variantName; }
        public void setVariantName(String variantName) { this.variantName = variantName; }
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
        public BigDecimal getPriceAtBuy() { return priceAtBuy; }
        public void setPriceAtBuy(BigDecimal priceAtBuy) { this.priceAtBuy = priceAtBuy; }
    }
}