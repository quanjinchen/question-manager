package cn.spring.arch.console.service;

import cn.spring.arch.common.page.PageData;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.CreateOrgReqParam;
import cn.spring.arch.console.pojo.req.DeleteOrgReqParam;
import cn.spring.arch.console.pojo.req.GetOrgByIdReqParam;
import cn.spring.arch.console.pojo.req.ListOrgReqParam;
import cn.spring.arch.console.pojo.req.UpdateOrgReqParam;
import cn.spring.arch.console.pojo.resp.OrgDTO;

import java.util.List;

public interface OrgService {

    RespInfo<List<OrgDTO>> listAllOrgTree();

    RespInfo<PageData<OrgDTO>> listOrg(ListOrgReqParam reqParam);

    RespInfo<OrgDTO> getOrgById(GetOrgByIdReqParam reqParam);

    RespInfo<OrgDTO> createOrg(CreateOrgReqParam reqParam);

    RespInfo<OrgDTO> updateOrg(UpdateOrgReqParam reqParam);

    RespInfo<Void> deleteOrg(DeleteOrgReqParam reqParam);
}

