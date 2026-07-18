package com.shopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopping.entity.Category;
import com.shopping.entity.Product;
import com.shopping.exception.BusinessException;
import com.shopping.mapper.ProductMapper;
import com.shopping.service.CategoryService;
import com.shopping.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    private CategoryService categoryService;

    @Override
    public IPage<Product> getProductPage(Long categoryId, String keyword, Integer status, Integer pageNum, Integer pageSize) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectProductPage(page, categoryId, keyword, status);
    }

    @Override
    public Product getProductDetail(Long id) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 查询分类名称
        if (product.getCategoryId() != null) {
            Category category = categoryService.getById(product.getCategoryId());
            if (category != null) {
                product.setCategoryName(category.getName());
            }
        }

        return product;
    }

    @Override
    public List<Product> getRecommendProducts(Integer limit) {
        return list(new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1)
                .orderByDesc(Product::getSales)
                .last("LIMIT " + limit));
    }

    @Override
    public List<Product> getNewProducts(Integer limit) {
        return list(new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1)
                .orderByDesc(Product::getCreateTime)
                .last("LIMIT " + limit));
    }
}
