package cn.spring.arch.console.service.impl;

import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.GetUserRoleListReqParam;
import cn.spring.arch.console.pojo.req.GrantUserRolesReqParam;
import cn.spring.arch.console.pojo.resp.UserRoleInfo;
import cn.spring.arch.console.service.UserRoleService;
import cn.spring.arch.system.entity.SysRole;
import cn.spring.arch.system.entity.SysRoleUser;
import cn.spring.arch.system.manager.SysRoleManager;
import cn.spring.arch.system.manager.SysRoleUserManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Resource
    private SysRoleUserManager sysRoleUserManager;
    @Resource
    private SysRoleManager sysRoleManager;

    @Override
    public RespInfo<List<UserRoleInfo>> listUserRole(GetUserRoleListReqParam reqParam) {
        return RespInfo.success(buildUserRoleInfoList(reqParam.getUserId()));
    }

    @Override
    public RespInfo<List<UserRoleInfo>> grantUserRoles(GrantUserRolesReqParam reqParam) {
        sysRoleUserManager.replaceUserRoles(reqParam.getUserId(), reqParam.getRoleIds());
        return RespInfo.success(buildUserRoleInfoList(reqParam.getUserId()));
    }

    private List<UserRoleInfo> buildUserRoleInfoList(Long userId) {
        List<SysRoleUser> roleUsers = sysRoleUserManager.listByUserId(userId);
        if (roleUsers.isEmpty()) {
            return Collections.emptyList();
        }
        List<UserRoleInfo> result = new ArrayList<UserRoleInfo>();
        for (SysRoleUser roleUser : roleUsers) {
            SysRole role = sysRoleManager.getById(roleUser.getRoleId());
            if (role == null) {
                continue;
            }
            UserRoleInfo roleInfo = new UserRoleInfo();
            roleInfo.setUserId(userId);
            roleInfo.setRoleId(role.getId());
            roleInfo.setRoleCode(role.getCode());
            roleInfo.setRoleName(role.getName());
            result.add(roleInfo);
        }
        return result;
    }
}

