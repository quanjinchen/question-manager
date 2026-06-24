package cn.spring.arch.framework.satoken;

import cn.spring.arch.common.constant.ResultCode;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.common.utils.JsonUtils;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoRedisJackson;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SaTokenConfig {

    private final ServletFilterConfig servletFilterConfig;

    public SaTokenConfig(ServletFilterConfig servletFilterConfig) {
        this.servletFilterConfig = servletFilterConfig;
    }

    @Bean
    public StpLogic stpLogic() {
        return new StpLogicJwtForSimple();
    }

    @Bean
    public SaTokenDao saTokenDao() {
        return new SaTokenDaoRedisJackson();
    }

    @Bean
    public SaServletFilter saServletFilter() {
        String[] excludePaths = servletFilterConfig.getExcludePaths();
        return new SaServletFilter()
                .addInclude("/**")
                .addExclude(excludePaths)
                .setAuth(obj -> {
                    String requestPath = SaHolder.getRequest().getRequestPath();
                    if (isExcluded(excludePaths, requestPath)) {
                        return;
                    }
                    StpUtil.checkLogin();
                })
                .setError(error -> {
                    log.warn("sa-token filter error: {}", error.getMessage(), error);
                    SaHolder.getResponse().setHeader("Content-Type", "application/json;charset=UTF-8");
                    return JsonUtils.toJson(RespInfo.failed(ResultCode.UNAUTHORIZED));
                });
    }

    private boolean isExcluded(String[] excludePaths, String requestPath) {
        for (String excludePath : excludePaths) {
            if (SaRouter.isMatch(excludePath, requestPath)) {
                return true;
            }
        }
        return false;
    }
}

