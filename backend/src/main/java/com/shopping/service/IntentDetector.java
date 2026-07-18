package com.shopping.service;

import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

/**
 * 意图识别器：判断用户输入属于哪类意图
 */
@Service
public class IntentDetector {

    public enum Intent {
        CHAT,          // 普通聊天
        SEARCH_PRODUCT, // 查商品
        QUERY_ORDER,   // 查订单
        KNOWLEDGE,     // 查知识库
        TOOL_CALL;     // 调用工具
    }

    public Intent detect(String message) {
        if (message == null || message.isBlank()) return Intent.CHAT;

        String msg = message.toLowerCase();

        // 查订单
        if (matches(msg, ".*(订单|物流|快递|发货|收货|退货|退款).*")) {
            return Intent.QUERY_ORDER;
        }
        // 查商品 / 搜索
        if (matches(msg, ".*(推荐|商品|买.*?|搜索|找|有没有|多少钱|价格|有没有|哪个好).*")) {
            return Intent.SEARCH_PRODUCT;
        }
        // 知识库
        if (matches(msg, ".*(规则|政策|运费|售后|退换货|保修|怎么|如何|帮助|指南).*")) {
            return Intent.KNOWLEDGE;
        }
        // 工具调用
        if (matches(msg, ".*(下单|优惠券|领券|签到|收藏|加入购物车).*")) {
            return Intent.TOOL_CALL;
        }
        return Intent.CHAT;
    }

    private boolean matches(String msg, String regex) {
        try { return Pattern.compile(regex).matcher(msg).find(); }
        catch (Exception e) { return false; }
    }
}
