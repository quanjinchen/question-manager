package cn.spring.arch.console.service;

import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.GetUserRoleListReqParam;
import cn.spring.arch.console.pojo.req.GrantUserRolesReqParam;
import cn.spring.arch.console.pojo.resp.UserRoleInfo;

import java.util.List;

public interface UserRoleService {

    RespInfo<List<UserRoleInfo>> listUserRole(GetUserRoleListReqParam reqParam);

    RespInfo<List<UserRoleInfo>> grantUserRoles(GrantUserRolesReqParam reqParam);
}

