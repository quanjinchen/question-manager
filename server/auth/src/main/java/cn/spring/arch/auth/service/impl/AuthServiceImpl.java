package cn.spring.arch.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.spring.arch.auth.config.AuthProperties;
import cn.spring.arch.auth.manager.CertTokenManager;
import cn.spring.arch.auth.pojo.cache.CertTokenCache;
import cn.spring.arch.auth.pojo.req.AccountQueryReqParam;
import cn.spring.arch.auth.pojo.req.AccountRegisterReqParam;
import cn.spring.arch.auth.pojo.req.CheckCertTokenReqParam;
import cn.spring.arch.auth.pojo.req.GetAuthResultReqParam;
import cn.spring.arch.auth.pojo.req.GetCertTokenReqParam;
import cn.spring.arch.auth.pojo.resp.AccountLoginDTO;
import cn.spring.arch.auth.pojo.resp.AppInfoDTO;
import cn.spring.arch.auth.pojo.resp.AuthResultDTO;
import cn.spring.arch.auth.pojo.resp.CertTokenDTO;
import cn.spring.arch.auth.pojo.resp.CertUserDTO;
import cn.spring.arch.auth.service.AuthService;
import cn.spring.arch.common.constant.ResultCode;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.framework.satoken.LoginUserContext;
import cn.spring.arch.system.entity.App;
import cn.spring.arch.system.entity.User;
import cn.spring.arch.system.manager.AppManager;
import cn.spring.arch.system.manager.UserManager;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private UserManager userManager;
    @Resource
    private CertTokenManager certTokenManager;
    @Resource
    private AuthProperties authProperties;
    @Resource
    private AppManager appManager;

    @Override
    public RespInfo<AccountLoginDTO> queryAccount(AccountQueryReqParam reqParam) {
        CertTokenCache certTokenCache = certTokenManager.getByCertToken(reqParam.getCertToken());
        ResultCode.AUTH_CERT_TOKEN_INVALID.assertNotNull(certTokenCache);

        User user = userManager.getByIdCard(reqParam.getIdCard());
        ResultCode.USER_NOT_FOUND.assertNotNull(user);
        ResultCode.USER_NOT_FOUND.assertIsTrue(reqParam.getFullName().equals(user.getFullName()));

        StpUtil.login(user.getId());
        StpUtil.getSession().set(LoginUserContext.LOGIN_USERNAME_SESSION_KEY, user.getUsername());
        return RespInfo.success(buildAccountLoginDTO(certTokenCache, buildCertUserDTO(user)));
    }

    @Override
    public RespInfo<AccountLoginDTO> registerAccount(AccountRegisterReqParam reqParam) {
        CertTokenCache certTokenCache = certTokenManager.getByCertToken(reqParam.getCertToken());
        ResultCode.AUTH_CERT_TOKEN_INVALID.assertNotNull(certTokenCache);

        User user = userManager.getByIdCard(reqParam.getIdCard());
        ResultCode.USER_NOT_FOUND.assertNotNull(user);
        ResultCode.USER_NOT_FOUND.assertIsTrue(reqParam.getFullName().equals(user.getFullName()));

        StpUtil.login(user.getId());
        StpUtil.getSession().set(LoginUserContext.LOGIN_USERNAME_SESSION_KEY, user.getUsername());
        CertUserDTO certUserDTO = buildCertUserDTO(user);
        certTokenCache.setAuthenticated(true);
        certTokenCache.setUserInfo(certUserDTO);
        certTokenManager.save(certTokenCache, authProperties.getCertTokenExpireSeconds());
        return RespInfo.success(buildAccountLoginDTO(certTokenCache, certUserDTO));
    }

    @Override
    public RespInfo<CertTokenDTO> getCertToken(GetCertTokenReqParam reqParam) {
        App app = appManager.getByClientId(reqParam.getClientId());
        ResultCode.APP_CLIENT_AUTH_FAILED.assertNotNull(app);
        ResultCode.APP_CLIENT_AUTH_FAILED.assertIsTrue(reqParam.getClientSecret().equals(app.getClientSecret()));

        String certToken = IdUtil.fastSimpleUUID();

        CertTokenCache certTokenCache = new CertTokenCache();
        certTokenCache.setCertToken(certToken);
        certTokenCache.setAppId(app.getId());
        certTokenCache.setAppName(app.getAppName());
        certTokenCache.setClientId(app.getClientId());
        certTokenCache.setAuthenticated(false);
        certTokenManager.save(certTokenCache, authProperties.getCertTokenExpireSeconds());

        CertTokenDTO certTokenDTO = new CertTokenDTO();
        certTokenDTO.setCertToken(certToken);
        certTokenDTO.setAuthUrl(authProperties.getAuthUrlPrefix() + "?certToken=" + certToken);
        certTokenDTO.setAppInfo(buildAppInfoDTO(certTokenCache));
        return RespInfo.success(certTokenDTO);
    }

    @Override
    public RespInfo<Void> checkCertToken(CheckCertTokenReqParam reqParam) {
        CertTokenCache certTokenCache = certTokenManager.getByCertToken(reqParam.getCertToken());
        ResultCode.AUTH_CERT_TOKEN_INVALID.assertNotNull(certTokenCache);
        return RespInfo.success();
    }

    @Override
    public RespInfo<AuthResultDTO> getAuthResult(GetAuthResultReqParam reqParam) {
        CertTokenCache certTokenCache = certTokenManager.getByCertToken(reqParam.getCertToken());
        ResultCode.AUTH_CERT_TOKEN_INVALID.assertNotNull(certTokenCache);

        AuthResultDTO authResultDTO = buildAuthResult(certTokenCache);
        if (Boolean.TRUE.equals(authResultDTO.getAuthenticated())) {
            certTokenManager.deleteByCertToken(reqParam.getCertToken());
        }
        return RespInfo.success(authResultDTO);
    }

    private AuthResultDTO buildAuthResult(CertTokenCache certTokenCache) {
        AuthResultDTO authResultDTO = new AuthResultDTO();
        authResultDTO.setCertToken(certTokenCache.getCertToken());
        CertUserDTO userInfo = certTokenCache.getUserInfo();
        boolean authenticated = Boolean.TRUE.equals(certTokenCache.getAuthenticated()) && userInfo != null;
        authResultDTO.setAuthenticated(authenticated);
        authResultDTO.setUserInfo(authenticated ? userInfo : null);
        return authResultDTO;
    }

    private AccountLoginDTO buildAccountLoginDTO(CertTokenCache certTokenCache, CertUserDTO certUserDTO) {
        AccountLoginDTO accountLoginDTO = new AccountLoginDTO();
        accountLoginDTO.setToken(StpUtil.getTokenValue());
        accountLoginDTO.setAppInfo(buildAppInfoDTO(certTokenCache));
        accountLoginDTO.setUserInfo(certUserDTO);
        return accountLoginDTO;
    }

    private AppInfoDTO buildAppInfoDTO(CertTokenCache certTokenCache) {
        AppInfoDTO appInfoDTO = new AppInfoDTO();
        appInfoDTO.setAppName(certTokenCache.getAppName());
        appInfoDTO.setClientId(certTokenCache.getClientId());
        return appInfoDTO;
    }

    private CertUserDTO buildCertUserDTO(User user) {
        CertUserDTO certUserDTO = new CertUserDTO();
        certUserDTO.setUserId(user.getId());
        certUserDTO.setFullName(user.getFullName());
        certUserDTO.setIdCard(user.getIdCard() == null ? null : user.getIdCard().getPlainText());
        certUserDTO.setPhone(user.getPhone() == null ? null : user.getPhone().getPlainText());
        return certUserDTO;
    }
}
