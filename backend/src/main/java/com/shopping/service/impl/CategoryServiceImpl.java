package com.shopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopping.entity.Category;
import com.shopping.mapper.CategoryMapper;
import com.shopping.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> getCategoryTree() {
        // 1. 查询所有分类
        List<Category> allCategories = list(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort));

        // 2. 获取一级分类
        List<Category> topCategories = allCategories.stream()
                .filter(c -> c.getParentId() == 0)
                .collect(Collectors.toList());

        // 3. 递归构建树形结构
        buildCategoryTree(topCategories, allCategories);

        return topCategories;
    }

    @Override
    public List<Category> getTopCategories() {
        return list(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, 0)
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort));
    }

    @Override
    public List<Category> getChildren(Long parentId) {
        return list(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, parentId)
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort));
    }

    /**
     * 递归构建分类树
     */
    private void buildCategoryTree(List<Category> parents, List<Category> allCategories) {
        for (Category parent : parents) {
            List<Category> children = allCategories.stream()
                    .filter(c -> c.getParentId().equals(parent.getId()))
                    .collect(Collectors.toList());

            parent.setChildren(children);

            if (!children.isEmpty()) {
                buildCategoryTree(children, allCategories);
            }
        }
    }
}
