package k23cnt2.nhom4.prj4.ttcd.controller;

import k23cnt2.nhom4.prj4.ttcd.entity.Category;
import k23cnt2.nhom4.prj4.ttcd.entity.Product;
import k23cnt2.nhom4.prj4.ttcd.repository.AdminCategoryRepository;
import k23cnt2.nhom4.prj4.ttcd.repository.ProductOptionRepository;
import k23cnt2.nhom4.prj4.ttcd.repository.ProductRepository;
import k23cnt2.nhom4.prj4.ttcd.repository.ProductVariantRepository;
import k23cnt2.nhom4.prj4.ttcd.service.AdminCategoryService;
import k23cnt2.nhom4.prj4.ttcd.service.AdminProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class AdminProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AdminCategoryRepository categoryRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private AdminProductService productService;

    @Autowired
    private AdminCategoryService categoryService;

    @GetMapping("/admin/products")
    public String productPage(Model model){

        model.addAttribute(
                "products",
                productRepository.findAll()
        );

        model.addAttribute(
                "categories",
                categoryRepository.findAll()
        );

        model.addAttribute(
                "variants",
                productVariantRepository.findAll()
        );

        model.addAttribute(
                "options",
                productOptionRepository.findAll()
        );

        return "Admin/admin-products";
    }
    @PostMapping("/admin/products/save")
    public String saveProduct(

            @RequestParam("name") String name,

            @RequestParam("slug") String slug,

            @RequestParam("price") BigDecimal price,

            @RequestParam("description") String description,

            @RequestParam("categoryId") Integer categoryId,

            @RequestParam("imageFile")
            MultipartFile imageFile

    ) throws IOException {

        String uploadDir =
                "src/main/resources/static/images/products/";

        String fileName =
                imageFile.getOriginalFilename();

        Path path =
                Paths.get(uploadDir + fileName);

        Files.write(path, imageFile.getBytes());

        Category category =
                categoryService.getCategoryById(categoryId);

        Product product = new Product();

        product.setName(name);

        product.setSlug(slug);

        product.setBasePrice(price);

        product.setDescription(description);

        product.setImageUrl(
                "/images/products/" + fileName
        );

        product.setIsActive(true);

        product.setCategory(category);

        productService.createProduct(product);

        return "redirect:/admin/products";
    }

    @PostMapping("/admin/products/save/{id}")
    public String updateProduct(

            @PathVariable Integer id,

            @RequestParam("name") String name,

            @RequestParam("slug") String slug,

            @RequestParam("price") BigDecimal price,

            @RequestParam("description") String description,

            @RequestParam("categoryId") Integer categoryId,

            @RequestParam(value = "imageFile",
                    required = false)
            MultipartFile imageFile

    ) throws IOException {

        Product product =
                productService.getProductById(id);

        product.setName(name);

        product.setSlug(slug);

        product.setBasePrice(price);

        product.setDescription(description);

        product.setCategory(
                categoryService.getCategoryById(categoryId)
        );

        if(imageFile != null &&
                !imageFile.isEmpty()) {

            String uploadDir =
                    "src/main/resources/static/images/products/";

            String fileName =
                    imageFile.getOriginalFilename();

            Path path =
                    Paths.get(uploadDir + fileName);

            Files.write(path, imageFile.getBytes());

            product.setImageUrl(
                    "/images/products/" + fileName
            );
        }

        productService.createProduct(product);

        return "redirect:/admin/products";
    }
}