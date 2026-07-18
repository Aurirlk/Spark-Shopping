package com.shopping.controller;

import com.shopping.service.SmartSearchService;
import com.shopping.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 智能搜索控制器
 * 向量搜索 + 网络搜索 + 混合搜索
 */
@RestController
@RequestMapping("/search")
public class SmartSearchController {

    @Autowired
    private SmartSearchService smartSearchService;

    /**
     * 向量搜索（语义搜索）
     */
    @GetMapping("/vector")
    public Result<List<Map<String, Object>>> vectorSearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int limit) {
        return Result.success(smartSearchService.vectorSearch(query, limit));
    }

    /**
     * 网络搜索
     */
    @GetMapping("/web")
    public Result<List<Map<String, Object>>> webSearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int limit) {
        return Result.success(smartSearchService.webSearch(query, limit));
    }

    /**
     * 混合搜索（推荐）
     */
    @GetMapping("/hybrid")
    public Result<List<Map<String, Object>>> hybridSearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        return Result.success(smartSearchService.hybridSearch(query, limit, categoryId, minPrice, maxPrice));
    }

    /**
     * 搜索建议 + 热门搜索
     */
    @GetMapping("/suggest")
    public Result<Map<String, Object>> getSuggestions(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") int limit) {
        return Result.success(smartSearchService.getSearchSuggestions(keyword, limit));
    }
}
