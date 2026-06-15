package cn.spring.arch.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "project.auth")
public class AuthProperties {

    /**
     * 认证页面访问地址，用于拼接 certToken 返回给调用方。
     */
    private String authUrlPrefix = "https://xxxx.com/auth";

    /**
     * certToken 缓存过期时间，单位秒。
     */
    private Long certTokenExpireSeconds = 600L;

    /**
     * 人脸认证通过阈值。
     */
    private Float faceMatchThreshold = 0.75F;
}
