package k23cnt2.nhom4.prj4.ttcd.controller;

import k23cnt2.nhom4.prj4.ttcd.dto.ProductDTO;
import k23cnt2.nhom4.prj4.ttcd.repository.ProductRepository;
import k23cnt2.nhom4.prj4.ttcd.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductDetails(@PathVariable int id) {
        return ResponseEntity.ok("Chi tiet san pham " + id);
    }


}
