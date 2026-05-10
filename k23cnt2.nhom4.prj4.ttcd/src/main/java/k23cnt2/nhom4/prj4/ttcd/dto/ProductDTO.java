package k23cnt2.nhom4.prj4.ttcd.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {
    private int  productId;
    private String name;
    private String imageUrl;
    private String description;
    private BigDecimal startingPrice;
    private double averageRating;
    private long totalReviews;

    public ProductDTO(int productId, String name, String imageUrl, String description, BigDecimal startingPrice, Double averageRating, Long totalReviews) {
        this.productId = productId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
        this.startingPrice = startingPrice != null ? startingPrice : BigDecimal.ZERO;
        this.averageRating = averageRating  != null ? averageRating : 0.0;
        this.totalReviews = totalReviews != null ? totalReviews : 0L;
    }
}
