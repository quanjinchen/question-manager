package cn.spring.arch.console.controller;

import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.GetAccessTokenReqParam;
import cn.spring.arch.console.pojo.resp.AccessTokenDTO;
import cn.spring.arch.console.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@Tag(name = "开放鉴权")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @Operation(summary = "获取应用访问令牌")
    @PostMapping("/get-access-token")
    public RespInfo<AccessTokenDTO> getAccessToken(@Valid @RequestBody GetAccessTokenReqParam reqParam) {
        return authService.getAccessToken(reqParam);
    }
}

