package com.shopping.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shopping.entity.Product;

import java.util.List;

public interface ProductService extends IService<Product> {

    /**
     * 分页查询商品
     */
    IPage<Product> getProductPage(Long categoryId, String keyword, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 获取商品详情
     */
    Product getProductDetail(Long id);

    /**
     * 获取推荐商品
     */
    List<Product> getRecommendProducts(Integer limit);

    /**
     * 获取新品上架
     */
    List<Product> getNewProducts(Integer limit);
}
