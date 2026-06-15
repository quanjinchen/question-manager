package cn.spring.arch.auth.manager.impl;

import cn.spring.arch.auth.manager.CertTokenManager;
import cn.spring.arch.auth.pojo.cache.CertTokenCache;
import cn.spring.arch.framework.redis.manager.RedisManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CertTokenManagerImpl implements CertTokenManager {

    private static final String CERT_TOKEN_CACHE_KEY_PREFIX = "auth:cert-token:";

    @Resource
    private RedisManager redisManager;

    @Override
    public void save(CertTokenCache certTokenCache, long expireSeconds) {
        redisManager.set(buildCacheKey(certTokenCache.getCertToken()), certTokenCache, expireSeconds);
    }

    @Override
    public CertTokenCache getByCertToken(String certToken) {
        return redisManager.get(buildCacheKey(certToken), CertTokenCache.class);
    }

    @Override
    public void deleteByCertToken(String certToken) {
        redisManager.delete(buildCacheKey(certToken));
    }

    private String buildCacheKey(String certToken) {
        return CERT_TOKEN_CACHE_KEY_PREFIX + certToken;
    }
}
