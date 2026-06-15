package cn.spring.arch.console.service;

import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.GetOrgUserListReqParam;
import cn.spring.arch.console.pojo.req.GrantOrgUsersReqParam;
import cn.spring.arch.console.pojo.resp.OrgUserInfo;

import java.util.List;

public interface OrgUserService {

    RespInfo<List<OrgUserInfo>> listOrgUser(GetOrgUserListReqParam reqParam);

    RespInfo<List<OrgUserInfo>> grantOrgUsers(GrantOrgUsersReqParam reqParam);
}

