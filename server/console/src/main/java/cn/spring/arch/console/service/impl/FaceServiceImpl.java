package cn.spring.arch.console.service.impl;

import cn.spring.arch.common.constant.ResultCode;
import cn.spring.arch.common.exception.BizException;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.CompareFaceFeatureReqParam;
import cn.spring.arch.console.pojo.req.ExtractFaceFeatureReqParam;
import cn.spring.arch.console.pojo.req.OpenFaceCompareReqParam;
import cn.spring.arch.console.pojo.req.OpenFaceSearchReqParam;
import cn.spring.arch.console.pojo.resp.FaceCompareDTO;
import cn.spring.arch.console.pojo.resp.FaceFeatureDTO;
import cn.spring.arch.console.pojo.resp.OpenFaceCompareDTO;
import cn.spring.arch.console.pojo.resp.OpenFaceSearchDTO;
import cn.spring.arch.console.pojo.resp.OpenFaceSearchUserDTO;
import cn.spring.arch.console.service.FaceService;
import cn.spring.arch.framework.face.FaceFeatureEngine;
import cn.spring.arch.framework.async.AsyncManager;
import cn.spring.arch.framework.auth.AccessTokenInfo;
import cn.spring.arch.framework.auth.AccessTokenManager;
import cn.spring.arch.framework.web.trace.TraceContext;
import cn.spring.arch.framework.web.trace.TraceContextHolder;
import cn.spring.arch.system.entity.FaceAuthLog;
import cn.spring.arch.system.entity.User;
import cn.spring.arch.system.enums.FaceAuthApiTypeEnum;
import cn.spring.arch.system.manager.FaceAuthLogManager;
import cn.spring.arch.system.manager.UserManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
public class FaceServiceImpl implements FaceService {

    @Resource
    private FaceFeatureEngine faceFeatureEngine;
    @Resource
    private AccessTokenManager accessTokenManager;
    @Resource
    private AsyncManager asyncManager;
    @Resource
    private FaceAuthLogManager faceAuthLogManager;
    @Resource
    private UserManager userManager;

    private static final float FACE_MATCH_THRESHOLD = 0.75F;

    @Override
    public RespInfo<FaceFeatureDTO> extractFaceFeature(ExtractFaceFeatureReqParam reqParam) {
        FaceFeatureDTO faceFeatureDTO = new FaceFeatureDTO();
        faceFeatureDTO.setFeature(faceFeatureEngine.extractFeatureBase64(reqParam.getImageBase64()));
        return RespInfo.success(faceFeatureDTO);
    }

    @Override
    public RespInfo<FaceCompareDTO> compareFaceFeature(CompareFaceFeatureReqParam reqParam) {
        FaceCompareDTO faceCompareDTO = new FaceCompareDTO();
        faceCompareDTO.setSimilarity(faceFeatureEngine.compare(reqParam.getSourceImageBase64(), reqParam.getTargetImageBase64()));
        return RespInfo.success(faceCompareDTO);
    }

    @Override
    public RespInfo<OpenFaceCompareDTO> openCompareFace(OpenFaceCompareReqParam reqParam) {
        FaceAuthLog faceAuthLog = createFaceAuthLog(reqParam.getAccessToken(), FaceAuthApiTypeEnum.ONE_TO_ONE);
        try {
            checkAccessToken(reqParam.getAccessToken());

            User user = userManager.getByIdCard(reqParam.getIdCard());
            ResultCode.USER_NOT_FOUND.assertNotNull(user);
            ResultCode.FACE_FEATURE_NOT_FOUND.assertIsTrue(StringUtils.hasText(user.getFaceFeature()));

            float[] inputFeatures = faceFeatureEngine.extractFeatureArray(reqParam.getFaceImageBase64());
            float[] storedFeatures = decodeFeature(reqParam.getIdCard(), user.getFaceFeature());
            float score = faceFeatureEngine.compare(inputFeatures, storedFeatures);
            boolean matched = score >= FACE_MATCH_THRESHOLD;

            OpenFaceCompareDTO result = new OpenFaceCompareDTO();
            result.setMatched(matched);
            result.setScore(score);

            if (matched) {
                faceAuthLog.setStatus(1);
                faceAuthLog.setAuthUserId(user.getId());
                faceAuthLog.setAuthFullName(user.getFullName());
            } else {
                faceAuthLog.setStatus(0);
                faceAuthLog.setErrmsg("人脸比对未通过");
            }
            return RespInfo.success(result);
        } catch (BizException exception) {
            faceAuthLog.setStatus(0);
            faceAuthLog.setErrmsg(exception.getMsg());
            throw exception;
        } catch (Exception exception) {
            faceAuthLog.setStatus(0);
            faceAuthLog.setErrmsg(ResultCode.INTERNAL_SERVER_ERROR.getMessage());
            throw exception;
        } finally {
            saveFaceAuthLogAsync(faceAuthLog);
        }
    }

    @Override
    public RespInfo<OpenFaceSearchDTO> openSearchFace(OpenFaceSearchReqParam reqParam) {
        FaceAuthLog faceAuthLog = createFaceAuthLog(reqParam.getAccessToken(), FaceAuthApiTypeEnum.ONE_TO_N);
        try {
            checkAccessToken(reqParam.getAccessToken());

            List<User> users = userManager.listUsersWithFaceFeature();
            ResultCode.FACE_FEATURE_NOT_FOUND.assertIsFalse(users.isEmpty());

            float[] inputFeatures = faceFeatureEngine.extractFeatureArray(reqParam.getFaceImageBase64());
            User bestUser = null;
            float bestScore = -1F;

            for (User user : users) {
                if (!StringUtils.hasText(user.getFaceFeature())) {
                    continue;
                }
                float[] storedFeatures = decodeFeature(String.valueOf(user.getId()), user.getFaceFeature());
                float score = faceFeatureEngine.compare(inputFeatures, storedFeatures);
                if (score > bestScore) {
                    bestScore = score;
                    bestUser = user;
                }
            }

            boolean matched = bestUser != null && bestScore >= FACE_MATCH_THRESHOLD;
            OpenFaceSearchDTO result = new OpenFaceSearchDTO();
            result.setMatched(matched);
            result.setScore(bestUser == null ? 0F : bestScore);
            if (matched) {
                OpenFaceSearchUserDTO userInfo = new OpenFaceSearchUserDTO();
                userInfo.setIdCard(bestUser.getIdCard() == null ? null : bestUser.getIdCard().getPlainText());
                userInfo.setPhoneNum(bestUser.getPhone() == null ? null : bestUser.getPhone().getPlainText());
                userInfo.setFullName(bestUser.getFullName());
                userInfo.setUserName(bestUser.getUsername());
                result.setUserInfo(userInfo);

                faceAuthLog.setStatus(1);
                faceAuthLog.setAuthUserId(bestUser.getId());
                faceAuthLog.setAuthFullName(bestUser.getFullName());
            } else {
                faceAuthLog.setStatus(0);
                faceAuthLog.setErrmsg("未匹配到认证用户");
            }
            return RespInfo.success(result);
        } catch (BizException exception) {
            faceAuthLog.setStatus(0);
            faceAuthLog.setErrmsg(exception.getMsg());
            throw exception;
        } catch (Exception exception) {
            faceAuthLog.setStatus(0);
            faceAuthLog.setErrmsg(ResultCode.INTERNAL_SERVER_ERROR.getMessage());
            throw exception;
        } finally {
            saveFaceAuthLogAsync(faceAuthLog);
        }
    }

    private void checkAccessToken(String accessToken) {
        ResultCode.FACE_ACCESS_TOKEN_INVALID.assertIsTrue(accessTokenManager.checkAccessToken(accessToken));
    }

    private FaceAuthLog createFaceAuthLog(String accessToken, FaceAuthApiTypeEnum authApiType) {
        FaceAuthLog faceAuthLog = new FaceAuthLog();
        faceAuthLog.setAuthApiType(authApiType);
        TraceContext traceContext = TraceContextHolder.get();
        if (traceContext != null) {
            faceAuthLog.setIp(traceContext.getClientIp());
        }
        AccessTokenInfo accessTokenInfo = accessTokenManager.getAccessToken(accessToken);
        if (accessTokenInfo == null || accessTokenInfo.getAppId() == null) {
            return faceAuthLog;
        }
        faceAuthLog.setAppId(accessTokenInfo.getAppId());
        faceAuthLog.setAppName(accessTokenInfo.getAppName());
        return faceAuthLog;
    }

    private void saveFaceAuthLogAsync(FaceAuthLog faceAuthLog) {
        asyncManager.execute(() -> {
            try {
                faceAuthLogManager.save(faceAuthLog);
            } catch (Exception exception) {
                log.error("save face auth log failed: {}", exception.getMessage(), exception);
            }
        });
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

