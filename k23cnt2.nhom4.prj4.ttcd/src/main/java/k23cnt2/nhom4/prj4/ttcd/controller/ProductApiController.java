package k23cnt2.nhom4.prj4.ttcd.controller;

import k23cnt2.nhom4.prj4.ttcd.dto.ProductDTO;
import k23cnt2.nhom4.prj4.ttcd.dto.ProductPopupDTO;
import k23cnt2.nhom4.prj4.ttcd.entity.Product;
import k23cnt2.nhom4.prj4.ttcd.entity.ProductOption;
import k23cnt2.nhom4.prj4.ttcd.entity.ProductVariant;
import k23cnt2.nhom4.prj4.ttcd.repository.ProductRepository;
import k23cnt2.nhom4.prj4.ttcd.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/products")
public class ProductApiController {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductService productService;

    @GetMapping("/home")
    public ResponseEntity<List<ProductDTO>> getHomeProducts() {
        try {
            List<ProductDTO> products = productService.getHomeProducts();

            if(products.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<?> getProductDetails(@PathVariable int id) {
//        return ResponseEntity.ok("Chi tiet san pham " + id);
//    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterMenu(
            @RequestParam(required = false) List<Integer> categoryIds,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<String> sizes){

        if ( sizes != null && sizes.isEmpty()) { sizes = null;}
        if ( categoryIds != null && categoryIds.isEmpty()) categoryIds = null;
        return ResponseEntity.ok(productService.getFilterdProducts(categoryIds, minPrice, maxPrice, keyword, sizes));

    }

    @GetMapping("/{id}/details") //
    public ResponseEntity<?> getProductDetailsForPopup(@PathVariable Integer id) {
        Optional<Product> productOpt = productService.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();

            List<ProductPopupDTO.VariantDTO> variantDTOs = product.getProductVariants().stream()
                    .map(v -> new ProductPopupDTO.VariantDTO(
                            v.getId(),
                            v.getSizeName(),
                            v.getExtraPrice()
                    )).toList();

            List<ProductPopupDTO.OptionDTO> optionDTOs = product.getOptions().stream()
                    .map(o -> new ProductPopupDTO.OptionDTO(
                            o.getId(),
                            o.getOptionName(),
                            o.getAdditionalPrice()
                    )).toList();

            ProductPopupDTO responseDTO = new ProductPopupDTO(
                    product.getId(),
                    product.getName(),
                    product.getBasePrice(),
                    product.getImageUrl(),
                    variantDTOs,
                    optionDTOs
            );

            return ResponseEntity.ok(responseDTO);
        }
        return ResponseEntity.notFound().build();
    }
}
