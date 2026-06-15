package cn.spring.arch.console.service;

import cn.spring.arch.common.page.PageData;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.CreateRoleReqParam;
import cn.spring.arch.console.pojo.req.DeleteRoleReqParam;
import cn.spring.arch.console.pojo.req.GrantRoleMenusReqParam;
import cn.spring.arch.console.pojo.req.GrantRoleUsersReqParam;
import cn.spring.arch.console.pojo.req.GetRoleUserListReqParam;
import cn.spring.arch.console.pojo.req.ListRoleAssignableUsersReqParam;
import cn.spring.arch.console.pojo.req.ListRoleReqParam;
import cn.spring.arch.console.pojo.req.UpdateRoleReqParam;
import cn.spring.arch.console.pojo.resp.RoleDTO;
import cn.spring.arch.console.pojo.resp.RoleGrantInfoDTO;
import cn.spring.arch.console.pojo.resp.RoleUserInfo;
import cn.spring.arch.console.pojo.resp.UserDTO;

import java.util.List;

public interface RoleService {

    RespInfo<PageData<RoleDTO>> listRole(ListRoleReqParam reqParam);

    RespInfo<RoleDTO> getRoleById(Long roleId);

    RespInfo<RoleDTO> createRole(CreateRoleReqParam reqParam);

    RespInfo<RoleDTO> updateRole(UpdateRoleReqParam reqParam);

    RespInfo<Void> deleteRole(DeleteRoleReqParam reqParam);

    RespInfo<RoleGrantInfoDTO> getRoleGrantInfoByRoleId(Long roleId);

    RespInfo<RoleGrantInfoDTO> grantRoleMenus(GrantRoleMenusReqParam reqParam);

    RespInfo<PageData<UserDTO>> listRoleAssignableUsers(ListRoleAssignableUsersReqParam reqParam);

    RespInfo<List<RoleUserInfo>> listRoleUsers(GetRoleUserListReqParam reqParam);

    RespInfo<List<RoleUserInfo>> grantRoleUsers(GrantRoleUsersReqParam reqParam);
}

