package cn.spring.arch.auth.manager;

import cn.spring.arch.auth.pojo.cache.CertTokenCache;

public interface CertTokenManager {

    void save(CertTokenCache certTokenCache, long expireSeconds);

    CertTokenCache getByCertToken(String certToken);

    void deleteByCertToken(String certToken);
}
