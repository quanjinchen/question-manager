package cn.spring.arch.auth.controller;

import cn.spring.arch.auth.pojo.req.AccountQueryReqParam;
import cn.spring.arch.auth.pojo.req.AccountRegisterReqParam;
import cn.spring.arch.auth.pojo.req.CheckCertTokenReqParam;
import cn.spring.arch.auth.pojo.req.FaceCompareReqParam;
import cn.spring.arch.auth.pojo.req.GetAuthResultReqParam;
import cn.spring.arch.auth.pojo.req.GetCertTokenReqParam;
import cn.spring.arch.auth.pojo.resp.AccountLoginDTO;
import cn.spring.arch.auth.pojo.resp.AuthResultDTO;
import cn.spring.arch.auth.pojo.resp.CertTokenDTO;
import cn.spring.arch.auth.service.AuthService;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.framework.operationlog.annotation.OperateLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@Tag(name = "认证平台")
@RestController
@RequestMapping("/face")
public class AuthController {

    @Resource
    private AuthService authService;

    @Operation(summary = "账户查询")
    @PostMapping("/query-account")
    @OperateLog(module = "认证平台", action = "账户查询")
    public RespInfo<AccountLoginDTO> queryAccount(@Valid @RequestBody AccountQueryReqParam reqParam) {
        return authService.queryAccount(reqParam);
    }

    @Operation(summary = "账户注册")
    @PostMapping("/register-account")
    @OperateLog(module = "认证平台", action = "账户注册")
    public RespInfo<AccountLoginDTO> registerAccount(@Valid @RequestBody AccountRegisterReqParam reqParam) {
        return authService.registerAccount(reqParam);
    }

    @Operation(summary = "人脸比对")
    @PostMapping("/compare-face")
    @OperateLog(module = "认证平台", action = "人脸比对")
    public RespInfo<AuthResultDTO> compareFace(@Valid @RequestBody FaceCompareReqParam reqParam) {
        return authService.compareFace(reqParam);
    }

    @Operation(summary = "获取 certToken")
    @PostMapping("/get-cert-token")
    public RespInfo<CertTokenDTO> getCertToken(@Valid @RequestBody GetCertTokenReqParam reqParam) {
        return authService.getCertToken(reqParam);
    }

    @Operation(summary = "校验 certToken")
    @PostMapping("/check-certToken")
    public RespInfo<Void> checkCertToken(@Valid @RequestBody CheckCertTokenReqParam reqParam) {
        return authService.checkCertToken(reqParam);
    }

    @Operation(summary = "查询认证结果")
    @PostMapping("/get-auth-result")
    public RespInfo<AuthResultDTO> getAuthResult(@Valid @RequestBody GetAuthResultReqParam reqParam) {
        return authService.getAuthResult(reqParam);
    }
}
