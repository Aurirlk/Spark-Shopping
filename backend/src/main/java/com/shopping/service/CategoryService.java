package com.shopping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shopping.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {

    /**
     * 获取分类树形结构
     */
    List<Category> getCategoryTree();

    /**
     * 获取一级分类列表
     */
    List<Category> getTopCategories();

    /**
     * 获取子分类列表
     */
    List<Category> getChildren(Long parentId);
}
