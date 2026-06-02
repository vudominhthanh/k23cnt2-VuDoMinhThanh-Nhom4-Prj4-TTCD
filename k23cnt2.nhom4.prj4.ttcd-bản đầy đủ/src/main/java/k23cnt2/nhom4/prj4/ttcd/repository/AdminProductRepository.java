package k23cnt2.nhom4.prj4.ttcd.repository;

import jakarta.transaction.Transactional;
import k23cnt2.nhom4.prj4.ttcd.dto.ProductDTO;
import k23cnt2.nhom4.prj4.ttcd.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdminProductRepository extends JpaRepository<Product, Integer> {

    @Query("Select new k23cnt2.nhom4.prj4.ttcd.dto.ProductDTO(p.id, p.name, p.imageUrl, p.description, MIN(p.basePrice + COALESCE(v.extraPrice, 0)), COALESCE(AVG(r.rating), 0), COUNT(DISTINCT r.id)) " +
            "from Product p " +
            "LEFT JOIN p.productVariants v " +
            "LEFT JOIN p.reviews r " +
            "WHERE p.isActive = true " +
            "GROUP BY p.id, p.name, p.imageUrl, p.description")
    List<ProductDTO> getProducts();
    @Transactional
    @Modifying
    @Query(value = "UPDATE n4_product SET n4_isActive = 0 WHERE n4_id = :id", nativeQuery = true)
    void softDeleteProductById(@Param("id") Integer id);

    boolean existsBySlug(String slug);
}