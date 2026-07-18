package com.shopping.service;

public interface LLMService {
    String chat(String message);
    String chat(String message, String modelName);
}
