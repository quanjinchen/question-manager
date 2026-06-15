package cn.spring.arch.console.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.GetUserRoleListReqParam;
import cn.spring.arch.console.pojo.req.GrantUserRolesReqParam;
import cn.spring.arch.console.pojo.resp.UserRoleInfo;
import cn.spring.arch.console.service.UserRoleService;
import cn.spring.arch.framework.operationlog.annotation.OperateLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Tag(name = "用户角色管理")
@RestController
@RequestMapping("/user-role")
public class UserRoleController {

    @Resource
    private UserRoleService userRoleService;

    @Operation(summary = "查询用户角色列表", description = "权限：system:user:query")
    @PostMapping("/list-user-role")
    @SaCheckPermission("system:user:query")
    public RespInfo<List<UserRoleInfo>> listUserRole(@Valid @RequestBody GetUserRoleListReqParam reqParam) {
        return userRoleService.listUserRole(reqParam);
    }

    @Operation(summary = "分配用户角色", description = "权限：system:user:update")
    @PostMapping("/grant-user-roles")
    @OperateLog(module = "用户角色管理", action = "分配用户角色")
    @SaCheckPermission("system:user:update")
    public RespInfo<List<UserRoleInfo>> grantUserRoles(@Valid @RequestBody GrantUserRolesReqParam reqParam) {
        return userRoleService.grantUserRoles(reqParam);
    }
}

