package com.jake.messagesystem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CacheService {
    private static final Logger log = LoggerFactory.getLogger(CacheService.class);

    private final StringRedisTemplate stringRedisTemplate;

    public CacheService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public String buildKey(String prefix, String key) {
        return "%s:%s".formatted(prefix, key);
    }

    public boolean delete(Collection<String> keys) {
        try {
            stringRedisTemplate.delete(keys);

            return true;
        } catch (Exception e) {
            log.error("Redis delete failed. keys: {}, cause: {}", keys, e.getMessage());
        }

        return false;
    }
}
