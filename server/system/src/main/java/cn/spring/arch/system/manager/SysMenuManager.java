package cn.spring.arch.system.manager;

import cn.spring.arch.system.entity.SysMenu;

import java.util.Collection;
import java.util.List;

public interface SysMenuManager {

    List<SysMenu> listAll();

    SysMenu getById(Long menuId);

    List<SysMenu> listByIds(Collection<Long> menuIds);

    SysMenu save(SysMenu menu);

    boolean existsChildren(Long menuId);

    void deleteById(Long menuId);
}

