package cn.spring.arch.console.service;

import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.SysAdminLoginReqParam;
import cn.spring.arch.console.pojo.resp.CaptchaData;
import cn.spring.arch.console.pojo.resp.LoginData;
import cn.spring.arch.console.pojo.resp.SysAdminLoginData;

public interface AdminAuthService {

    RespInfo<CaptchaData> getCaptcha();

    RespInfo<SysAdminLoginData> login(SysAdminLoginReqParam reqParam);

    RespInfo<LoginData> getLoginInfo();

    RespInfo<Void> logout();
}

