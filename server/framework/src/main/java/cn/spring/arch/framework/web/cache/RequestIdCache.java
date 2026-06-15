package cn.spring.arch.framework.web.cache;

public interface RequestIdCache {

    boolean saveRequestId(String requestId, long expireSeconds);
}

