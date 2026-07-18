package com.shopping.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "llm")
public class LLMConfig {
    private String defaultModel = "openai";
    private List<ModelConfig> models;

    @Data
    public static class ModelConfig {
        private String name;
        private String provider;   // openai, azure, ollama, claude
        private String apiKey;
        private String baseUrl;
        private String model;
        private Double temperature = 0.7;
        private Integer maxTokens = 2048;
        private boolean enabled = true;
    }
}
