package cn.spring.arch.framework.captcha;

public interface CaptchaCache {

    void put(String uuid, String code, int expireSeconds);

    String get(String uuid);

    void remove(String uuid);
}

