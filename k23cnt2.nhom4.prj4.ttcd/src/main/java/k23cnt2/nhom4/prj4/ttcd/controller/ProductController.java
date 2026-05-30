package k23cnt2.nhom4.prj4.ttcd.controller;

import k23cnt2.nhom4.prj4.ttcd.entity.Product;
import k23cnt2.nhom4.prj4.ttcd.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/product-detail")
    public String productDetails(@RequestParam("id") Integer id, Model model) {
        Optional<Product> productOpt = productService.findById(id);

        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            model.addAttribute("product", product);

            model.addAttribute("variants", product.getProductVariants());

            model.addAttribute("toppings", product.getOptions().stream().filter(o -> "TOPPING".equals(o.getOptionType())).toList());
            model.addAttribute("sugars", product.getOptions().stream().filter(o -> "SUGAR".equals(o.getOptionType())).toList());
            model.addAttribute("ices", product.getOptions().stream().filter(o -> "ICE".equals(o.getOptionType())).toList());

            return "/User/product_details.html";
        }
        return "redirect:/menu";
    }
}
