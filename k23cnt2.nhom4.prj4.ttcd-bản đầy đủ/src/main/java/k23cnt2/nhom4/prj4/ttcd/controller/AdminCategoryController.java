package k23cnt2.nhom4.prj4.ttcd.controller;

import k23cnt2.nhom4.prj4.ttcd.service.AdminCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminCategoryController {

    @Autowired
    private AdminCategoryService categoryService;

    @GetMapping("/admin/categories")
    public String categoryPage(Model model) {

        model.addAttribute(
                "categories",
                categoryService.getAllCategories()
        );

        return "Admin/admin-categories";
    }
}