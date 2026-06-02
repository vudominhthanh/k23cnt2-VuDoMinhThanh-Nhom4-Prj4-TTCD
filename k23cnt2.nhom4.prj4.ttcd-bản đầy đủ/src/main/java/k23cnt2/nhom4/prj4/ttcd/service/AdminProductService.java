package k23cnt2.nhom4.prj4.ttcd.service;

import k23cnt2.nhom4.prj4.ttcd.dto.ProductDTO;
import k23cnt2.nhom4.prj4.ttcd.entity.Category;
import k23cnt2.nhom4.prj4.ttcd.entity.Product;
import k23cnt2.nhom4.prj4.ttcd.entity.ProductVariant;
import k23cnt2.nhom4.prj4.ttcd.repository.AdminCategoryRepository;
import k23cnt2.nhom4.prj4.ttcd.repository.AdminProductRepository;
import k23cnt2.nhom4.prj4.ttcd.repository.AdminProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class AdminProductService {

    @Autowired
    private AdminProductRepository productRepository;

    @Autowired
    private AdminCategoryRepository categoryRepository;

    @Autowired
    private AdminProductVariantRepository productVariantRepository;


    public List<ProductDTO> getHomeProducts() {
        return productRepository.getProducts();
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }


    public Product getProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }


    @Transactional
    public Product saveProductWithVariants(String name, String slug, Double basePrice, String description,
                                           Integer categoryId, MultipartFile imageFile, String variantIdsRaw) {

        if (productRepository.existsBySlug(slug)) {
            throw new RuntimeException("Đường dẫn (Slug) '" + slug + "' đã tồn tại! Vui lòng nhập slug khác.");
        }
        Product product = new Product();
        product.setName(name);
        product.setSlug(slug);


        product.setBasePrice(BigDecimal.valueOf(basePrice));
        product.setDescription(description);
        product.setIsActive(true);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục có ID: " + categoryId));
        product.setCategory(category);

        // Xử lý lưu tên file ảnh
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = "/images/products/" + imageFile.getOriginalFilename();
            product.setImageUrl(imageUrl);
        } else {
            product.setImageUrl("/images/products/default.png");
        }


        Product savedProduct = productRepository.save(product);

        if (variantIdsRaw != null && !variantIdsRaw.trim().isEmpty()) {
            String[] variantIds = variantIdsRaw.split(",");
            for (String vId : variantIds) {
                Integer optionId = Integer.parseInt(vId.trim());

                ProductVariant newVariant = new ProductVariant();
                newVariant.setProduct(savedProduct);

                if (optionId == 4) {
                    newVariant.setSizeName("M");
                    newVariant.setExtraPrice(BigDecimal.valueOf(0.0));
                } else if (optionId == 5) {
                    newVariant.setSizeName("L");
                    newVariant.setExtraPrice(BigDecimal.valueOf(10000.0));
                } else if (optionId == 6) {
                    newVariant.setSizeName("XL");
                    newVariant.setExtraPrice(BigDecimal.valueOf(15000.0));
                }

                productVariantRepository.save(newVariant);
            }
        }
        return savedProduct;
    }

    // Sửa sản phẩm cơ bản (JSON)
    public Product updateProduct(Integer id, Product newProduct) {
        Product oldProduct = getProductById(id);
        oldProduct.setName(newProduct.getName());
        oldProduct.setSlug(newProduct.getSlug());
        oldProduct.setDescription(newProduct.getDescription());
        oldProduct.setImageUrl(newProduct.getImageUrl());
        oldProduct.setBasePrice(newProduct.getBasePrice());
        oldProduct.setIsActive(newProduct.getIsActive());

        if (newProduct.getCategory() != null) {
            oldProduct.setCategory(newProduct.getCategory());
        }
        return productRepository.save(oldProduct);
    }

    // 🌟 THÊM MỚI: Cập nhật sản phẩm từ FormData (Có thay đổi file ảnh)
    @Transactional
    public Product updateProductWithImage(Integer id, String name, String slug, Double basePrice,
                                          String description, Integer categoryId, MultipartFile imageFile) {
        Product product = getProductById(id);

        product.setName(name);
        product.setSlug(slug);
        product.setBasePrice(BigDecimal.valueOf(basePrice)); // Chuyển đổi kiểu dữ liệu sang BigDecimal
        product.setDescription(description);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        product.setCategory(category);

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = "/images/products/" + imageFile.getOriginalFilename();
            product.setImageUrl(imageUrl);
        }
        return productRepository.save(product);
    }

    // Đảo trạng thái hoạt động (Ẩn/Hiện - Thay thế hoàn toàn cho xóa cứng)
    @Transactional
    public void toggleProductStatus(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm có ID: " + id));

        product.setIsActive(!product.getIsActive());
        productRepository.save(product);
    }

    // Xóa mềm sản phẩm
    public void deleteProduct(Integer id) {
        productRepository.softDeleteProductById(id);
    }
}