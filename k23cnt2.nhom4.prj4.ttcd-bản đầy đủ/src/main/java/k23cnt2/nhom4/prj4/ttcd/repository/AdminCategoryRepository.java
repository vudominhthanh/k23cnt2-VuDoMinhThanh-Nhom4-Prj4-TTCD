package k23cnt2.nhom4.prj4.ttcd.repository;

import k23cnt2.nhom4.prj4.ttcd.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminCategoryRepository extends JpaRepository<Category, Integer> {
    boolean existsBySlug(String slug);
}