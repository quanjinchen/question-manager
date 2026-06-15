package cn.spring.arch.console.service.impl;

import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.GetOrgUserListReqParam;
import cn.spring.arch.console.pojo.req.GrantOrgUsersReqParam;
import cn.spring.arch.console.pojo.resp.OrgUserInfo;
import cn.spring.arch.console.service.OrgUserService;
import cn.spring.arch.system.entity.OrgUser;
import cn.spring.arch.system.entity.User;
import cn.spring.arch.system.manager.OrgUserManager;
import cn.spring.arch.system.manager.UserManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class OrgUserServiceImpl implements OrgUserService {

    @Resource
    private OrgUserManager orgUserManager;
    @Resource
    private UserManager userManager;

    @Override
    public RespInfo<List<OrgUserInfo>> listOrgUser(GetOrgUserListReqParam reqParam) {
        return RespInfo.success(buildOrgUserInfoList(reqParam.getOrgId()));
    }

    @Override
    public RespInfo<List<OrgUserInfo>> grantOrgUsers(GrantOrgUsersReqParam reqParam) {
        orgUserManager.replaceOrgUsers(reqParam.getOrgId(), reqParam.getUserIds());
        return RespInfo.success(buildOrgUserInfoList(reqParam.getOrgId()));
    }

    private List<OrgUserInfo> buildOrgUserInfoList(Long orgId) {
        List<OrgUser> orgUsers = orgUserManager.listByOrgId(orgId);
        if (orgUsers.isEmpty()) {
            return Collections.emptyList();
        }

        List<OrgUserInfo> result = new ArrayList<OrgUserInfo>();
        for (OrgUser orgUser : orgUsers) {
            User user = userManager.getById(orgUser.getUserId());
            if (user == null) {
                continue;
            }
            OrgUserInfo info = new OrgUserInfo();
            info.setOrgId(orgId);
            info.setUserId(user.getId());
            info.setUsername(user.getUsername());
            info.setFullName(user.getFullName());
            result.add(info);
        }
        return result;
    }
}

