package com.shopping.service;

import cn.dev33.satoken.stp.StpUtil;
import com.shopping.entity.Product;
import com.shopping.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 本地 Agent：意图识别 → 知识检索 → 工具调用 → 回复合成
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LocalAgent {

    private final IntentDetector intentDetector;
    private final ProductMapper productMapper;
    private final LLMService llmService;

    /**
     * 处理用户消息
     */
    public Map<String, Object> process(String message, Long userId) {
        IntentDetector.Intent intent = intentDetector.detect(message);
        Map<String, Object> result = new HashMap<>();
        result.put("intent", intent.name());
        result.put("userId", userId);

        switch (intent) {
            case SEARCH_PRODUCT:
                result.putAll(handleSearchProduct(message));
                break;
            case QUERY_ORDER:
                result.putAll(handleQueryOrder(userId));
                break;
            case KNOWLEDGE:
                result.putAll(handleKnowledge(message));
                break;
            case TOOL_CALL:
                result.putAll(handleToolCall(message, userId));
                break;
            default:
                result.putAll(handleChat(message));
                break;
        }
        return result;
    }

    /** 搜索商品 */
    private Map<String, Object> handleSearchProduct(String message) {
        Map<String, Object> r = new HashMap<>();
        String keyword = message.replaceAll("(推荐|商品|买|搜索|找|多少钱|价格|哪个好)", "").trim();
        if (keyword.length() < 2) keyword = "热门";

        List<Product> products = productMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Product>()
                        .like(Product::getName, keyword).eq(Product::getStatus, 1)
                        .last("LIMIT 5"));
        if (products.isEmpty()) {
            r.put("message", "没有找到「" + keyword + "」相关的商品，试试其他关键词？");
        } else {
            String names = products.stream().map(Product::getName).collect(Collectors.joining("、"));
            r.put("message", "为您找到以下「" + keyword + "」相关商品：\n" + names);
            r.put("products", products.stream().map(p -> Map.of("id", p.getId(), "name", p.getName(), "price", p.getPrice())).collect(Collectors.toList()));
        }
        r.put("type", "search");
        return r;
    }

    /** 查询订单 */
    private Map<String, Object> handleQueryOrder(Long userId) {
        Map<String, Object> r = new HashMap<>();
        if (userId == null) {
            r.put("message", "请先登录再查询订单哦");
            return r;
        }
        r.put("message", "您的最近订单可以在「我的订单」中查看，需要帮您跳转吗？");
        r.put("type", "order");
        r.put("action", "redirect");
        r.put("url", "/order/list");
        return r;
    }

    /** 知识库 */
    private Map<String, Object> handleKnowledge(String question) {
        Map<String, Object> r = new HashMap<>();
        String answer = knowledgeBase(question);
        r.put("message", answer);
        r.put("type", "knowledge");
        return r;
    }

    /** 工具调用 */
    private Map<String, Object> handleToolCall(String message, Long userId) {
        Map<String, Object> r = new HashMap<>();
        if (message.contains("签到")) {
            r.put("message", "已为您跳转到签到页面，每天签到领积分！");
            r.put("action", "redirect");
            r.put("url", "/user");
        } else if (message.contains("优惠券") || message.contains("领券")) {
            r.put("message", "为您打开领券中心，看看有什么好券！");
            r.put("action", "redirect");
            r.put("url", "/coupon");
        } else {
            r.put("message", "好的，正在为您处理！");
        }
        r.put("type", "tool");
        return r;
    }

    /** LLM 聊天 */
    private Map<String, Object> handleChat(String message) {
        Map<String, Object> r = new HashMap<>();
        String reply = llmService.chat(message);
        r.put("message", reply);
        r.put("type", "chat");
        return r;
    }

    /** 内部知识库 */
    private String knowledgeBase(String question) {
        Map<String, String> kb = new HashMap<>();
        kb.put("运费", "全场满99元免运费，不满99元收取10元运费。");
        kb.put("退换货", "支持7天无理由退换货，质量问题包邮退换。");
        kb.put("保修", "所有商品享受国家三包政策，电子产品保修一年。");
        kb.put("发货", "下单后24小时内发货，预售商品以页面标注为准。");
        kb.put("客服", "工作时间9:00-21:00，可拨打400-123-4567。");

        for (Map.Entry<String, String> entry : kb.entrySet()) {
            if (question.contains(entry.getKey())) return entry.getValue();
        }
        return "这个问题让我想想...您可以咨询在线客服获取更详细的解答。";
    }
}
