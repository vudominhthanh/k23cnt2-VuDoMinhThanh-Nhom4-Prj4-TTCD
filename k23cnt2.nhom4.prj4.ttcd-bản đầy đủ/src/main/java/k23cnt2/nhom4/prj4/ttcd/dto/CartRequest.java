package k23cnt2.nhom4.prj4.ttcd.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartRequest {
    private Integer variantId;
    private Integer quantity;
    private List<Integer> optionIds;
}
