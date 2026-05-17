package k23cnt2.nhom4.prj4.ttcd.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProductPopupDTO {
    private Integer id;
    private String name;
    private BigDecimal basePrice;
    private String imageUrl;
    private List<VariantDTO> productVariants;
    private List<OptionDTO> productOptions;

    public ProductPopupDTO() {}

    public ProductPopupDTO(Integer id, String name, BigDecimal basePrice, String imageUrl,
                           List<VariantDTO> productVariants, List<OptionDTO> productOptions) {
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
        this.imageUrl = imageUrl;
        this.productVariants = productVariants;
        this.productOptions = productOptions;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<VariantDTO> getProductVariants() { return productVariants; }
    public void setProductVariants(List<VariantDTO> productVariants) { this.productVariants = productVariants; }

    public List<OptionDTO> getProductOptions() { return productOptions; }
    public void setProductOptions(List<OptionDTO> productOptions) { this.productOptions = productOptions; }

    public static class VariantDTO {
        private Integer id;
        private String sizeName;
        private BigDecimal extraPrice = BigDecimal.ZERO;

        public VariantDTO() {}
        public VariantDTO(Integer id, String sizeName, BigDecimal extraPrice) {
            this.id = id;
            this.sizeName = sizeName;
            this.extraPrice = extraPrice;
        }

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getSizeName() { return sizeName; }
        public void setSizeName(String sizeName) { this.sizeName = sizeName; }
        public BigDecimal getExtraPrice() { return extraPrice; }
        public void SetWExtraPrice(Long adjustmentPrice) { this.extraPrice = extraPrice; }
    }

    public static class OptionDTO {
        private Integer id;
        private String optionName;
        private BigDecimal additionalPrice;

        public OptionDTO() {}
        public OptionDTO(Integer id, String optionName, BigDecimal additionalPrice) {
            this.id = id;
            this.optionName = optionName;
            this.additionalPrice = additionalPrice;
        }

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getOptionName() { return optionName; }
        public void setOptionName(String optionName) { this.optionName = optionName; }
        public BigDecimal getAdditionalPrice() { return additionalPrice; }
        public void setAdditionalPrice(BigDecimal additionalPrice) { this.additionalPrice = additionalPrice; }
    }
}