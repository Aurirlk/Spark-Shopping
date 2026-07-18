package com.shopping.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtils {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void set(String key, String value) { redisTemplate.opsForValue().set(key, value); }
    public void set(String key, String value, long timeout, TimeUnit unit) { redisTemplate.opsForValue().set(key, value, timeout, unit); }
    public String get(String key) { return redisTemplate.opsForValue().get(key); }
    public Boolean delete(String key) { return redisTemplate.delete(key); }
    public Boolean hasKey(String key) { return redisTemplate.hasKey(key); }
    public Boolean expire(String key, long timeout, TimeUnit unit) { return redisTemplate.expire(key, timeout, unit); }
    public Long increment(String key) { return redisTemplate.opsForValue().increment(key); }
    public Long increment(String key, long delta) { return redisTemplate.opsForValue().increment(key, delta); }

    public <T> void setObject(String key, T value, long timeout, TimeUnit unit) {
        try { redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(value), timeout, unit); }
        catch (JsonProcessingException e) { log.error("序列化失败: {}", key, e); }
    }
    public <T> T getObject(String key, Class<T> clazz) {
        try { String json = redisTemplate.opsForValue().get(key); return json != null ? objectMapper.readValue(json, clazz) : null; }
        catch (JsonProcessingException e) { log.error("反序列化失败: {}", key, e); return null; }
    }

    public static String getUserTokenKey(Long userId) { return "user:token:" + userId; }
    public static String getUserInfoKey(Long userId) { return "user:info:" + userId; }
    public static String getProductKey(Long productId) { return "product:" + productId; }
    public static String getHotProductsKey() { return "product:hot"; }
    public static String getLockKey(String businessType, String businessId) { return "lock:" + businessType + ":" + businessId; }

    public boolean tryLock(String key, String value, long timeout, TimeUnit unit) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit));
    }
    public void unlock(String key) { redisTemplate.delete(key); }
}
