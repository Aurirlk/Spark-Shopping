package com.shopping.service;

import java.util.List;
import java.util.Map;

/**
 * 智能搜索服务 - 向量搜索 + 网络搜索
 */
public interface SmartSearchService {

    /**
     * 向量相似度搜索（基于Embedding）
     * 将用户查询转为向量，在商品库中找语义相似的商品
     */
    List<Map<String, Object>> vectorSearch(String query, int limit);

    /**
     * 网络搜索（集成搜索引擎）
     * 当本地商品库无匹配时，自动搜索网络商品信息
     */
    List<Map<String, Object>> webSearch(String query, int limit);

    /**
     * 混合搜索（向量 + 关键词 + 网络）
     * 综合多种搜索策略返回最佳结果
     */
    List<Map<String, Object>> hybridSearch(String query, int limit, Long categoryId, Double minPrice, Double maxPrice);

    /**
     * 获取搜索建议（自动补全 + 热门）
     */
    Map<String, Object> getSearchSuggestions(String keyword, int limit);
}
