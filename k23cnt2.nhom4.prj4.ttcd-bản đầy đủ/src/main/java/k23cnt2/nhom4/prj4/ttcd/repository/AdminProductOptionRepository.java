package k23cnt2.nhom4.prj4.ttcd.repository;

import k23cnt2.nhom4.prj4.ttcd.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminProductOptionRepository
        extends JpaRepository<ProductOption, Integer> {
}