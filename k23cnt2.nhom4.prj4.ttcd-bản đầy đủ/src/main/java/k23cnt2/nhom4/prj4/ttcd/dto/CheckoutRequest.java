package k23cnt2.nhom4.prj4.ttcd.dto;

import lombok.Data;

@Data
public class CheckoutRequest {
    private String shippingAddress;

    private String paymentMethod;
}