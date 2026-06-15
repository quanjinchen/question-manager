package cn.spring.arch.console.service.impl;

import cn.spring.arch.common.constant.ResultCode;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.GetAccessTokenReqParam;
import cn.spring.arch.console.pojo.resp.AccessTokenDTO;
import cn.spring.arch.console.service.AuthService;
import cn.spring.arch.framework.auth.AccessTokenInfo;
import cn.spring.arch.framework.auth.AccessTokenManager;
import cn.spring.arch.system.entity.App;
import cn.spring.arch.system.manager.AppManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AuthServiceImpl implements AuthService {

    private static final int ACCESS_TOKEN_EXPIRE_SECONDS = 7200;

    @Resource
    private AppManager appManager;
    @Resource
    private AccessTokenManager accessTokenManager;

    @Override
    public RespInfo<AccessTokenDTO> getAccessToken(GetAccessTokenReqParam reqParam) {
        App app = appManager.getByClientId(reqParam.getClientId());
        ResultCode.APP_CLIENT_AUTH_FAILED.assertNotNull(app);
        ResultCode.APP_CLIENT_AUTH_FAILED.assertIsTrue(reqParam.getClientSecret().equals(app.getClientSecret()));

        AccessTokenInfo accessTokenInfo = new AccessTokenInfo();
        accessTokenInfo.setAppId(app.getId());
        accessTokenInfo.setAppName(app.getAppName());
        accessTokenInfo.setExpiresIn(ACCESS_TOKEN_EXPIRE_SECONDS);
        String accessToken = accessTokenManager.getAccessToken(accessTokenInfo, ACCESS_TOKEN_EXPIRE_SECONDS);

        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setAccessToken(accessToken);
        accessTokenDTO.setExpiresIn(ACCESS_TOKEN_EXPIRE_SECONDS);
        return RespInfo.success(accessTokenDTO);
    }
}

