package k23cnt2.nhom4.prj4.ttcd.repository;

import k23cnt2.nhom4.prj4.ttcd.dto.ProductDTO;
import k23cnt2.nhom4.prj4.ttcd.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("Select new k23cnt2.nhom4.prj4.ttcd.dto.ProductDTO(p.id,p.name,p.imageUrl,p.description,MIN(p.basePrice + COALESCE(v.extraPrice, 0)), COALESCE(AVG(r.rating),0), COUNT( DISTINCT r.id))" +
            "from Product p " +
            "LEFT JOIN p.productVariants v " +
            "LEFT JOIN p.reviews r " +
            "GROUP BY p.id,p.name,p.imageUrl,p.description ")
    List<ProductDTO> getProducts();
}
