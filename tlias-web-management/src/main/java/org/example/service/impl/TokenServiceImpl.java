package org.example.service.impl;

import org.example.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Token 黑名单实现 — 使用 StringRedisTemplate 存储。
 */
@Service
public class TokenServiceImpl implements TokenService {

    private static final String BLACKLIST_PREFIX = "token:blacklist:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void blacklist(String token, long expireIn) {
        String key = BLACKLIST_PREFIX + token;
        stringRedisTemplate.opsForValue().set(key, "1", expireIn, TimeUnit.SECONDS);
    }

    @Override
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }
}
