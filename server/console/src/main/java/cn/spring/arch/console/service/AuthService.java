package cn.spring.arch.console.service;

import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.GetAccessTokenReqParam;
import cn.spring.arch.console.pojo.resp.AccessTokenDTO;

public interface AuthService {

    RespInfo<AccessTokenDTO> getAccessToken(GetAccessTokenReqParam reqParam);
}

