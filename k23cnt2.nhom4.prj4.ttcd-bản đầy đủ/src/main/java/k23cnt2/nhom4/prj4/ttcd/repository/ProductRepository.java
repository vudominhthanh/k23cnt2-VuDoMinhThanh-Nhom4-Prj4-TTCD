package k23cnt2.nhom4.prj4.ttcd.repository;

import k23cnt2.nhom4.prj4.ttcd.dto.ProductDTO;
import k23cnt2.nhom4.prj4.ttcd.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("Select new k23cnt2.nhom4.prj4.ttcd.dto.ProductDTO(p.id,p.name,p.imageUrl,p.description,MIN(p.basePrice + COALESCE(v.extraPrice, 0)), COALESCE(AVG(r.rating),0), COUNT( DISTINCT r.id))" +
            "from Product p " +
            "LEFT JOIN p.productVariants v " +
            "LEFT JOIN p.reviews r " +
            "GROUP BY p.id,p.name,p.imageUrl,p.description ")
    List<ProductDTO> getProducts();

    @Query("SELECT DISTINCT new k23cnt2.nhom4.prj4.ttcd.dto.ProductDTO(" +
            "p.id, p.name, p.imageUrl, p.description, p.basePrice, " +
            "COALESCE(AVG(r.rating), 0.0), COUNT(DISTINCT r.id)) " +
            "FROM Product p " +
            "LEFT JOIN p.productVariants v " +
            "LEFT JOIN p.reviews r " +
            "WHERE (:categoryIds IS NULL OR p.category.id IN :categoryIds) AND " +
            "(:minPrice IS NULL OR p.basePrice >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.basePrice <= :maxPrice) AND " +
            "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:sizes IS NULL OR v.sizeName IN :sizes) " +
            "GROUP BY p.id, p.name, p.imageUrl, p.description, p.basePrice")
    List<ProductDTO> filterRealTime(
            @Param("categoryIds") List<Integer> categoryIds,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("keyword") String keyword,
            @Param("sizes") List<String> sizes
    );

    @Override
    Optional<Product> findById(Integer integer);
}
