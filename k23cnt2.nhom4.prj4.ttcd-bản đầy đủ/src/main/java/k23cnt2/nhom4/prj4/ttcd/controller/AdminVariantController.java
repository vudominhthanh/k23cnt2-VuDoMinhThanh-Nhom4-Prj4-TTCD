package k23cnt2.nhom4.prj4.ttcd.controller;

import k23cnt2.nhom4.prj4.ttcd.entity.Product;
import k23cnt2.nhom4.prj4.ttcd.entity.ProductVariant;
import k23cnt2.nhom4.prj4.ttcd.repository.ProductRepository;
import k23cnt2.nhom4.prj4.ttcd.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
public class AdminVariantController {

    @Autowired
    private ProductVariantRepository variantRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/admin/variants")
    public String variantPage(Model model){

        model.addAttribute(
                "variants",
                variantRepository.findAll()
        );

        model.addAttribute(
                "products",
                productRepository.findAll()
        );

        return "Admin/admin-variants";
    }

    @PostMapping("/admin/variants/save")
    public String saveVariant(

            @RequestParam Integer productId,

            @RequestParam String sizeName,

            @RequestParam BigDecimal extraPrice
    ){

        Product product =
                productRepository.findById(productId)
                        .orElseThrow();

        ProductVariant variant =
                new ProductVariant();

        variant.setProduct(product);

        variant.setSizeName(sizeName);

        variant.setExtraPrice(extraPrice);

        variantRepository.save(variant);

        return "redirect:/admin/variants";
    }

    @PostMapping("/admin/variants/save/{id}")
    public String updateVariant(

            @PathVariable Integer id,

            @RequestParam Integer productId,

            @RequestParam String sizeName,

            @RequestParam BigDecimal extraPrice
    ){

        ProductVariant variant =
                variantRepository.findById(id)
                        .orElseThrow();

        Product product =
                productRepository.findById(productId)
                        .orElseThrow();

        variant.setProduct(product);

        variant.setSizeName(sizeName);

        variant.setExtraPrice(extraPrice);

        variantRepository.save(variant);

        return "redirect:/admin/variants";
    }

    @DeleteMapping("/api/admin/variants/{id}")
    @ResponseBody
    public void deleteVariant(
            @PathVariable Integer id
    ){
        variantRepository.deleteById(id);
    }
}