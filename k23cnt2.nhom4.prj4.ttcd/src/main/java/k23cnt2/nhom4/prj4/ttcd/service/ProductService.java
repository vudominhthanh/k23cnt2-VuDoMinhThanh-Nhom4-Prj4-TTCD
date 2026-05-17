package k23cnt2.nhom4.prj4.ttcd.service;

import k23cnt2.nhom4.prj4.ttcd.dto.ProductDTO;
import k23cnt2.nhom4.prj4.ttcd.entity.Product;
import k23cnt2.nhom4.prj4.ttcd.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductDTO> getHomeProducts() {
        return productRepository.getProducts();
    }

    public Optional<Product> findById(Integer id) {
        return productRepository.findById(id);
    }

    public List<ProductDTO> getFilterdProducts(List<Integer> categoryIds, BigDecimal min, BigDecimal max, String keyword, List<String> sizes) {

        return productRepository.filterRealTime(categoryIds, min, max, keyword, sizes);
    }
}
