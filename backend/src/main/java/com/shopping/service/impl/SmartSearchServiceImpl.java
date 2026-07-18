package com.shopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shopping.entity.Product;
import com.shopping.mapper.ProductMapper;
import com.shopping.service.SmartSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 智能搜索服务实现
 * 向量搜索: 基于SQL LIKE + 分类/价格过滤的语义近似搜索
 * 网络搜索: 模拟搜索结果 + 缓存热门
 */
@Service
public class SmartSearchServiceImpl implements SmartSearchService {

    @Autowired
    private ProductMapper productMapper;

    // 热门搜索词缓存
    private static final List<String> HOT_KEYWORDS = List.of(
        "iPhone", "华为", "小米", "耳机", "笔记本",
        "空调", "冰箱", "运动鞋", "卫衣", "手表"
    );

    @Override
    public List<Map<String, Object>> vectorSearch(String query, int limit) {
        List<Map<String, Object>> results = new ArrayList<>();

        // 1. 精确名称匹配（权重最高）
        LambdaQueryWrapper<Product> exactWrapper = new LambdaQueryWrapper<>();
        exactWrapper.eq(Product::getStatus, 1)
                .eq(Product::getName, query)
                .last("LIMIT " + limit);
        List<Product> exactMatches = productMapper.selectList(exactWrapper);

        for (Product p : exactMatches) {
            results.add(productToMap(p, 1.0));
        }

        if (results.size() >= limit) return results.subList(0, limit);

        // 2. 模糊名称匹配
        LambdaQueryWrapper<Product> fuzzyWrapper = new LambdaQueryWrapper<>();
        fuzzyWrapper.eq(Product::getStatus, 1)
                .like(Product::getName, query)
                .last("LIMIT " + (limit - results.size()));
        List<Product> fuzzyMatches = productMapper.selectList(fuzzyWrapper);

        Set<Long> existingIds = results.stream().map(r -> (Long) r.get("id")).collect(Collectors.toSet());
        for (Product p : fuzzyMatches) {
            if (!existingIds.contains(p.getId())) {
                results.add(productToMap(p, 0.8));
                existingIds.add(p.getId());
            }
        }

        if (results.size() >= limit) return results.subList(0, limit);

        // 3. 描述匹配
        LambdaQueryWrapper<Product> descWrapper = new LambdaQueryWrapper<>();
        descWrapper.eq(Product::getStatus, 1)
                .like(Product::getDescription, query)
                .last("LIMIT " + (limit - results.size()));
        List<Product> descMatches = productMapper.selectList(descWrapper);

        for (Product p : descMatches) {
            if (!existingIds.contains(p.getId())) {
                results.add(productToMap(p, 0.6));
                existingIds.add(p.getId());
            }
        }

        return results;
    }

    @Override
    public List<Map<String, Object>> webSearch(String query, int limit) {
        // 模拟网络搜索结果
        List<Map<String, Object>> results = new ArrayList<>();
        Random random = new Random(query.hashCode());

        for (int i = 0; i < Math.min(limit, 5); i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("title", query + " - 网络搜索结果" + (i + 1));
            item.put("source", "电商平台");
            item.put("price", Math.round(100 + random.nextDouble() * 9900) / 100.0);
            item.put("url", "https://search.example.com/" + query + "/" + i);
            item.put("description", "这是关于「" + query + "」的网络搜索结果，来自第三方电商平台。");
            results.add(item);
        }

        return results;
    }

    @Override
    public List<Map<String, Object>> hybridSearch(String query, int limit, Long categoryId, Double minPrice, Double maxPrice) {
        List<Map<String, Object>> results = new ArrayList<>();

        // 1. 向量搜索
        List<Map<String, Object>> vectorResults = vectorSearch(query, limit);
        results.addAll(vectorResults);

        Set<Long> existingIds = results.stream()
                .filter(r -> r.containsKey("id"))
                .map(r -> (Long) r.get("id"))
                .collect(Collectors.toSet());

        // 2. 分类/价格过滤
        if (results.size() < limit && (categoryId != null || minPrice != null || maxPrice != null)) {
            LambdaQueryWrapper<Product> filterWrapper = new LambdaQueryWrapper<>();
            filterWrapper.eq(Product::getStatus, 1);

            if (categoryId != null) filterWrapper.eq(Product::getCategoryId, categoryId);
            if (minPrice != null) filterWrapper.ge(Product::getPrice, minPrice);
            if (maxPrice != null) filterWrapper.le(Product::getPrice, maxPrice);
            filterWrapper.last("LIMIT " + (limit - results.size()));

            List<Product> filterMatches = productMapper.selectList(filterWrapper);
            for (Product p : filterMatches) {
                if (!existingIds.contains(p.getId())) {
                    results.add(productToMap(p, 0.5));
                    existingIds.add(p.getId());
                }
            }
        }

        return results;
    }

    @Override
    public Map<String, Object> getSearchSuggestions(String keyword, int limit) {
        Map<String, Object> result = new HashMap<>();

        // 自动补全建议
        List<String> suggestions = new ArrayList<>();
        if (keyword != null && !keyword.isEmpty()) {
            LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Product::getStatus, 1)
                    .like(Product::getName, keyword)
                    .last("LIMIT " + limit);
            suggestions = productMapper.selectList(wrapper).stream()
                    .map(Product::getName)
                    .collect(Collectors.toList());
        }

        result.put("suggestions", suggestions);
        result.put("hotKeywords", HOT_KEYWORDS);
        return result;
    }

    private Map<String, Object> productToMap(Product p, double score) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", p.getId());
        map.put("name", p.getName());
        map.put("price", p.getPrice());
        map.put("sales", p.getSales());
        map.put("mainImage", p.getMainImage());
        map.put("description", p.getDescription());
        map.put("score", score);
        return map;
    }
}
