package cn.spring.arch.auth.service;

import cn.spring.arch.auth.pojo.req.AccountQueryReqParam;
import cn.spring.arch.auth.pojo.req.AccountRegisterReqParam;
import cn.spring.arch.auth.pojo.req.CheckCertTokenReqParam;
import cn.spring.arch.auth.pojo.req.GetAuthResultReqParam;
import cn.spring.arch.auth.pojo.req.GetCertTokenReqParam;
import cn.spring.arch.auth.pojo.resp.AccountLoginDTO;
import cn.spring.arch.auth.pojo.resp.AuthResultDTO;
import cn.spring.arch.auth.pojo.resp.CertTokenDTO;
import cn.spring.arch.common.pojo.RespInfo;

public interface AuthService {

    RespInfo<AccountLoginDTO> queryAccount(AccountQueryReqParam reqParam);

    RespInfo<AccountLoginDTO> registerAccount(AccountRegisterReqParam reqParam);

    RespInfo<CertTokenDTO> getCertToken(GetCertTokenReqParam reqParam);

    RespInfo<Void> checkCertToken(CheckCertTokenReqParam reqParam);

    RespInfo<AuthResultDTO> getAuthResult(GetAuthResultReqParam reqParam);
}
