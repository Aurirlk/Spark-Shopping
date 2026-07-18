package com.shopping.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shopping.entity.Product;
import com.shopping.service.ProductService;
import com.shopping.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public Result<IPage<Product>> getProductList(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        IPage<Product> productPage = productService.getProductPage(categoryId, keyword, status, pageNum, pageSize);
        return Result.success(productPage);
    }

    @GetMapping("/detail/{id}")
    public Result<Product> getProductDetail(@PathVariable Long id) {
        Product product = productService.getProductDetail(id);
        return Result.success(product);
    }

    @GetMapping("/recommend")
    public Result<List<Product>> getRecommendProducts(
            @RequestParam(defaultValue = "8") Integer limit) {
        List<Product> products = productService.getRecommendProducts(limit);
        return Result.success(products);
    }

    @GetMapping("/new")
    public Result<List<Product>> getNewProducts(
            @RequestParam(defaultValue = "8") Integer limit) {
        List<Product> products = productService.getNewProducts(limit);
        return Result.success(products);
    }
}
