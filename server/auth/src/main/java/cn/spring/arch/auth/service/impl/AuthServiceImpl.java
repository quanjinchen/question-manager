package cn.spring.arch.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.spring.arch.auth.config.AuthProperties;
import cn.spring.arch.auth.manager.CertTokenManager;
import cn.spring.arch.auth.pojo.cache.CertTokenCache;
import cn.spring.arch.auth.pojo.req.AccountQueryReqParam;
import cn.spring.arch.auth.pojo.req.AccountRegisterReqParam;
import cn.spring.arch.auth.pojo.req.CheckCertTokenReqParam;
import cn.spring.arch.auth.pojo.req.FaceCompareReqParam;
import cn.spring.arch.auth.pojo.req.GetAuthResultReqParam;
import cn.spring.arch.auth.pojo.req.GetCertTokenReqParam;
import cn.spring.arch.auth.pojo.resp.AccountLoginDTO;
import cn.spring.arch.auth.pojo.resp.AppInfoDTO;
import cn.spring.arch.auth.pojo.resp.AuthResultDTO;
import cn.spring.arch.auth.pojo.resp.CertTokenDTO;
import cn.spring.arch.auth.pojo.resp.CertUserDTO;
import cn.spring.arch.auth.service.AuthService;
import cn.spring.arch.framework.face.FaceFeatureEngine;
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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;

@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private UserManager userManager;
    @Resource
    private FaceFeatureEngine faceFeatureEngine;
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
        return RespInfo.success(buildAccountLoginDTO(certTokenCache, user, null));
    }

    @Override
    public RespInfo<AccountLoginDTO> registerAccount(AccountRegisterReqParam reqParam) {
        CertTokenCache certTokenCache = certTokenManager.getByCertToken(reqParam.getCertToken());
        ResultCode.AUTH_CERT_TOKEN_INVALID.assertNotNull(certTokenCache);

        User user = userManager.getByIdCard(reqParam.getIdCard());
        ResultCode.USER_NOT_FOUND.assertNotNull(user);
        ResultCode.USER_NOT_FOUND.assertIsTrue(reqParam.getFullName().equals(user.getFullName()));

        user.setFaceFeature(faceFeatureEngine.extractFeatureBase64(reqParam.getFaceImageBase64()));
        userManager.save(user);

        StpUtil.login(user.getId());
        StpUtil.getSession().set(LoginUserContext.LOGIN_USERNAME_SESSION_KEY, user.getUsername());
        return RespInfo.success(buildAccountLoginDTO(certTokenCache, user, reqParam.getFaceImageBase64()));
    }

    @Override
    public RespInfo<AuthResultDTO> compareFace(FaceCompareReqParam reqParam) {
        CertTokenCache certTokenCache = certTokenManager.getByCertToken(reqParam.getCertToken());
        ResultCode.AUTH_CERT_TOKEN_INVALID.assertNotNull(certTokenCache);

        User user = userManager.getByIdCard(reqParam.getIdCard());
        ResultCode.USER_NOT_FOUND.assertNotNull(user);
        ResultCode.USER_NOT_FOUND.assertIsTrue(reqParam.getFullName().equals(user.getFullName()));
        ResultCode.FACE_FEATURE_NOT_FOUND.assertIsTrue(StringUtils.hasText(user.getFaceFeature()));

        float[] inputFeatures = faceFeatureEngine.extractFeatureArray(reqParam.getFaceImageBase64());
        float[] storedFeatures = decodeFeature(reqParam.getIdCard(), user.getFaceFeature());
        float score = faceFeatureEngine.compare(inputFeatures, storedFeatures);
        ResultCode.AUTH_FACE_NOT_MATCHED.assertIsTrue(score >= authProperties.getFaceMatchThreshold());

        CertUserDTO certUserDTO = buildCertUserDTO(user, reqParam.getFaceImageBase64());

        certTokenCache.setAuthenticated(true);
        certTokenCache.setUserInfo(certUserDTO);
        certTokenManager.save(certTokenCache, authProperties.getCertTokenExpireSeconds());
        return RespInfo.success(buildAuthResult(certTokenCache));
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
        boolean authenticated = userInfo != null && StringUtils.hasText(userInfo.getFaceImageBase64());
        authResultDTO.setAuthenticated(authenticated);
        authResultDTO.setUserInfo(authenticated ? userInfo : null);
        return authResultDTO;
    }

    private AccountLoginDTO buildAccountLoginDTO(CertTokenCache certTokenCache, User user, String faceImageBase64) {
        AccountLoginDTO accountLoginDTO = new AccountLoginDTO();
        accountLoginDTO.setToken(StpUtil.getTokenValue());
        accountLoginDTO.setAppInfo(buildAppInfoDTO(certTokenCache));
        accountLoginDTO.setUserInfo(buildCertUserDTO(user, faceImageBase64));
        accountLoginDTO.setFaceRegistered(StringUtils.hasText(user.getFaceFeature()));
        return accountLoginDTO;
    }

    private AppInfoDTO buildAppInfoDTO(CertTokenCache certTokenCache) {
        AppInfoDTO appInfoDTO = new AppInfoDTO();
        appInfoDTO.setAppName(certTokenCache.getAppName());
        appInfoDTO.setClientId(certTokenCache.getClientId());
        return appInfoDTO;
    }

    private CertUserDTO buildCertUserDTO(User user, String faceImageBase64) {
        CertUserDTO certUserDTO = new CertUserDTO();
        certUserDTO.setUserId(user.getId());
        certUserDTO.setFullName(user.getFullName());
        certUserDTO.setIdCard(user.getIdCard() == null ? null : user.getIdCard().getPlainText());
        certUserDTO.setPhone(user.getPhone() == null ? null : user.getPhone().getPlainText());
        certUserDTO.setFaceImageBase64(faceImageBase64);
        return certUserDTO;
    }

    private float[] decodeFeature(String bizId, String featureBase64) {
        try {
            byte[] bytes = Base64.getDecoder().decode(featureBase64);
            float[] result = new float[bytes.length / 4];
            ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().get(result);
            return result;
        } catch (Exception exception) {
            throw ResultCode.FACE_FEATURE_EXTRACT_FAILED.newException("用户特征值解析失败: " + bizId);
        }
    }
}
