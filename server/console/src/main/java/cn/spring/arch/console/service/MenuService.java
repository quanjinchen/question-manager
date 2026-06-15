package cn.spring.arch.console.service;

import cn.spring.arch.common.page.PageData;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.CreateMenuReqParam;
import cn.spring.arch.console.pojo.req.DeleteMenuReqParam;
import cn.spring.arch.console.pojo.req.GetMenuByIdReqParam;
import cn.spring.arch.console.pojo.req.ListMenuReqParam;
import cn.spring.arch.console.pojo.req.UpdateMenuReqParam;
import cn.spring.arch.console.pojo.resp.MenuDTO;
import cn.spring.arch.console.pojo.resp.MenuTreeNode;

import java.util.List;

public interface MenuService {

    RespInfo<List<MenuTreeNode>> listAllMenuTree();

    RespInfo<PageData<MenuDTO>> listMenu(ListMenuReqParam reqParam);

    RespInfo<MenuDTO> getMenuById(GetMenuByIdReqParam reqParam);

    RespInfo<MenuDTO> createMenu(CreateMenuReqParam reqParam);

    RespInfo<MenuDTO> updateMenu(UpdateMenuReqParam reqParam);

    RespInfo<Void> deleteMenu(DeleteMenuReqParam reqParam);

    List<MenuTreeNode> listMenuTreeByUserId(Long userId);
}

