package k23cnt2.nhom4.prj4.ttcd.service;

import k23cnt2.nhom4.prj4.ttcd.dto.ProductDTO;
import k23cnt2.nhom4.prj4.ttcd.entity.Product;
import k23cnt2.nhom4.prj4.ttcd.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductDTO> getHomeProducts() {
        return productRepository.getProducts();
    }
}
