package com.shopping.controller;

import com.shopping.entity.Category;
import com.shopping.service.CategoryService;
import com.shopping.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/tree")
    public Result<List<Category>> getCategoryTree() {
        List<Category> tree = categoryService.getCategoryTree();
        return Result.success(tree);
    }

    @GetMapping("/top")
    public Result<List<Category>> getTopCategories() {
        List<Category> categories = categoryService.getTopCategories();
        return Result.success(categories);
    }

    @GetMapping("/children/{parentId}")
    public Result<List<Category>> getChildren(@PathVariable Long parentId) {
        List<Category> categories = categoryService.getChildren(parentId);
        return Result.success(categories);
    }

    @GetMapping("/list")
    public Result<List<Category>> getCategoryList() {
        List<Category> categories = categoryService.list();
        return Result.success(categories);
    }
}
