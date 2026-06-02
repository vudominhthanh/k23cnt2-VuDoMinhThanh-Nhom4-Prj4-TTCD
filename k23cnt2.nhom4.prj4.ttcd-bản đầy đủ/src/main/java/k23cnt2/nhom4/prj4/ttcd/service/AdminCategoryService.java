package k23cnt2.nhom4.prj4.ttcd.service;

import k23cnt2.nhom4.prj4.ttcd.entity.Category;
import k23cnt2.nhom4.prj4.ttcd.repository.AdminCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
public class AdminCategoryService {

    @Autowired
    private AdminCategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
    }

    @Transactional
    public Category createCategoryWithImage(String name, String slug, MultipartFile imageFile) {
        if (categoryRepository.existsBySlug(slug)) {
            throw new RuntimeException("Slug danh mục đã tồn tại!");
        }

        Category category = new Category();
        category.setName(name);
        category.setSlug(slug);

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = "/images/categories/" + imageFile.getOriginalFilename();
            category.setImageUrl(imageUrl);
        } else {
            category.setImageUrl("/images/categories/default.png");
        }

        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategoryWithImage(Integer id, String name, String slug, MultipartFile imageFile) {
        Category category = getCategoryById(id);

        category.setName(name);
        category.setSlug(slug);

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = "/images/categories/" + imageFile.getOriginalFilename();
            category.setImageUrl(imageUrl);
        }

        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Danh mục không tồn tại hoặc đã bị xóa trước đó!");
        }
        categoryRepository.deleteById(id);
    }
}