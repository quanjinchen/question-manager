package cn.spring.arch.console.controller;

import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.SysAdminLoginReqParam;
import cn.spring.arch.console.pojo.resp.CaptchaData;
import cn.spring.arch.console.pojo.resp.LoginData;
import cn.spring.arch.console.pojo.resp.SysAdminLoginData;
import cn.spring.arch.console.service.AdminAuthService;
import cn.spring.arch.framework.operationlog.annotation.OperateLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "后台认证")
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminAuthService adminAuthService;

    public AdminController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @Operation(summary = "获取图形验证码")
    @PostMapping("/get-captcha")
    public RespInfo<CaptchaData> getCaptcha() {
        return adminAuthService.getCaptcha();
    }

    @Operation(summary = "账号密码登录")
    @PostMapping("/login")
    @OperateLog(module = "后台认证", action = "账号密码登录")
    public RespInfo<SysAdminLoginData> login(@Valid @RequestBody SysAdminLoginReqParam reqParam) {
        return adminAuthService.login(reqParam);
    }

    @Operation(summary = "获取当前登录信息")
    @PostMapping("/get-login-info")
    public RespInfo<LoginData> getLoginInfo() {
        return adminAuthService.getLoginInfo();
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    @OperateLog(module = "后台认证", action = "退出登录")
    public RespInfo<Void> logout() {
        return adminAuthService.logout();
    }
}

