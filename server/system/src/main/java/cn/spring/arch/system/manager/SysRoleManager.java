package cn.spring.arch.system.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.spring.arch.common.page.PageReqParam;
import cn.spring.arch.system.entity.SysRole;
import cn.spring.arch.system.pojo.query.ListRoleQuery;

import java.util.Collection;
import java.util.List;

public interface SysRoleManager {

    Page<SysRole> page(PageReqParam reqParam);

    SysRole getById(Long roleId);

    SysRole getByCode(String code);

    SysRole getByName(String name);

    List<SysRole> listByIds(Collection<Long> roleIds);

    List<SysRole> listRoles(ListRoleQuery query);

    SysRole save(SysRole role);

    void deleteById(Long roleId);
}

