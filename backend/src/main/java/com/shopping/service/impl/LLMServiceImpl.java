package com.shopping.service.impl;

import com.shopping.config.LLMConfig;
import com.shopping.service.LLMService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * 多模型 LLM 服务：支持 OpenAI、Ollama 等
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LLMServiceImpl implements LLMService {

    private final LLMConfig llmConfig;
    private final HttpClient http = HttpClient.newHttpClient();

    @Override
    public String chat(String message) {
        return chat(message, llmConfig.getDefaultModel());
    }

    @Override
    public String chat(String message, String modelName) {
        LLMConfig.ModelConfig model = getModel(modelName);
        if (model == null) return "AI模型未配置";

        try {
            String body = buildRequestBody(model, message);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(model.getBaseUrl() + "/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + model.getApiKey())
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            return parseResponse(res.body());
        } catch (Exception e) {
            log.error("LLM调用失败: {}", e.getMessage());
            // 降级到本地回复
            return fallbackReply(message);
        }
    }

    private String buildRequestBody(LLMConfig.ModelConfig model, String message) {
        return String.format("""
            {"model":"%s","messages":[{"role":"user","content":"%s"}],"temperature":%s,"max_tokens":%d}
            """, model.getModel(), message.replace("\"", "\\\""), model.getTemperature(), model.getMaxTokens());
    }

    private String parseResponse(String body) {
        try {
            // 简单解析：提取 content 字段
            int start = body.indexOf("\"content\":\"");
            if (start < 0) return fallbackReply("");
            start += 11;
            int end = body.indexOf("\"", start);
            return body.substring(start, end).replace("\\n", "\n");
        } catch (Exception e) {
            return fallbackReply("");
        }
    }

    /** 降级回复 */
    private String fallbackReply(String message) {
        if (message.contains("你好") || message.contains("嗨")) return "你好呀！有什么可以帮您的吗？😊";
        if (message.contains("推荐") || message.contains("买什么")) return "您可以看看我们的热销商品，有很多不错的选择！";
        if (message.contains("价格") || message.contains("多少钱")) return "商品价格在详情页都有标注，还有限时优惠哦！";
        return "好的，我在听。您可以问我商品信息、订单状态、优惠活动等问题。";
    }

    private LLMConfig.ModelConfig getModel(String name) {
        List<LLMConfig.ModelConfig> models = llmConfig.getModels();
        if (models == null || models.isEmpty()) return null;
        return models.stream().filter(m -> m.getName().equals(name) && m.isEnabled()).findFirst()
                .orElse(models.stream().filter(LLMConfig.ModelConfig::isEnabled).findFirst().orElse(null));
    }
}
