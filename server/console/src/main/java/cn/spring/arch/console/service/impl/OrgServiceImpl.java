package cn.spring.arch.console.service.impl;

import cn.spring.arch.common.page.PageData;
import cn.spring.arch.common.constant.ResultCode;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.CreateOrgReqParam;
import cn.spring.arch.console.pojo.req.DeleteOrgReqParam;
import cn.spring.arch.console.pojo.req.GetOrgByIdReqParam;
import cn.spring.arch.console.pojo.req.ListOrgReqParam;
import cn.spring.arch.console.pojo.req.UpdateOrgReqParam;
import cn.spring.arch.console.pojo.resp.OrgDTO;
import cn.spring.arch.console.service.OrgService;
import cn.spring.arch.system.entity.Org;
import cn.spring.arch.system.manager.OrgManager;
import cn.spring.arch.system.manager.OrgUserManager;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrgServiceImpl implements OrgService {

    @Resource
    private OrgManager orgManager;
    @Resource
    private OrgUserManager orgUserManager;

    @Override
    public RespInfo<List<OrgDTO>> listAllOrgTree() {
        List<Org> orgList = orgManager.listAll();
        List<OrgDTO> orgDTOList = new ArrayList<OrgDTO>(orgList.size());
        for (Org org : orgList) {
            orgDTOList.add(toOrgDTO(org));
        }
        return RespInfo.success(orgDTOList);
    }

    @Override
    public RespInfo<PageData<OrgDTO>> listOrg(ListOrgReqParam reqParam) {
        PageHelper.startPage(reqParam.getPageNum(), reqParam.getPageSize());
        List<Org> orgList = orgManager.listAll();
        List<OrgDTO> orgDTOList = new ArrayList<OrgDTO>(orgList.size());
        for (Org org : orgList) {
            orgDTOList.add(toOrgDTO(org));
        }

        PageData<OrgDTO> pageData = new PageData<OrgDTO>();
        pageData.setTotal(new PageInfo<Org>(orgList).getTotal());
        pageData.setRecords(orgDTOList);
        pageData.setPageNum(reqParam.getPageNum());
        pageData.setPageSize(reqParam.getPageSize());
        return RespInfo.success(pageData);
    }

    @Override
    public RespInfo<OrgDTO> getOrgById(GetOrgByIdReqParam reqParam) {
        Org org = orgManager.getById(reqParam.getOrgId());
        ResultCode.ORG_NOT_FOUND.assertNotNull(org);
        return RespInfo.success(toOrgDTO(org));
    }

    @Override
    public RespInfo<OrgDTO> createOrg(CreateOrgReqParam reqParam) {
        Org org = new Org();
        org.setParentId(reqParam.getParentId());
        org.setOrgCode(reqParam.getOrgCode());
        org.setName(reqParam.getName());
        org.setLeaderName(reqParam.getLeaderName());
        org.setSortOrder(reqParam.getSortOrder() == null ? 0 : reqParam.getSortOrder());
        org.setStatus(reqParam.getStatus() == null ? 1 : reqParam.getStatus());
        return RespInfo.created(toOrgDTO(orgManager.save(org)));
    }

    @Override
    public RespInfo<OrgDTO> updateOrg(UpdateOrgReqParam reqParam) {
        Org existedOrg = orgManager.getById(reqParam.getId());
        ResultCode.ORG_NOT_FOUND.assertNotNull(existedOrg);

        existedOrg.setParentId(reqParam.getParentId());
        existedOrg.setOrgCode(reqParam.getOrgCode());
        existedOrg.setName(reqParam.getName());
        existedOrg.setLeaderName(reqParam.getLeaderName());
        existedOrg.setSortOrder(reqParam.getSortOrder() == null ? 0 : reqParam.getSortOrder());
        existedOrg.setStatus(reqParam.getStatus() == null ? 1 : reqParam.getStatus());
        return RespInfo.success(toOrgDTO(orgManager.save(existedOrg)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RespInfo<Void> deleteOrg(DeleteOrgReqParam reqParam) {
        Long orgId = reqParam.getOrgId();
        Org org = orgManager.getById(orgId);
        ResultCode.ORG_NOT_FOUND.assertNotNull(org);
        ResultCode.DELETE_ORG_FAILED_BECAUSE_HAS_CHILD.assertIsFalse(orgManager.existsChildren(orgId));
        ResultCode.DELETE_ORG_FAILED_BECAUSE_HAS_USER.assertIsFalse(orgUserManager.existsByOrgId(orgId));
        orgUserManager.deleteByOrgId(orgId);
        orgManager.deleteById(orgId);
        return RespInfo.success();
    }

    private OrgDTO toOrgDTO(Org org) {
        OrgDTO orgDTO = new OrgDTO();
        orgDTO.setId(org.getId());
        orgDTO.setParentId(org.getParentId());
        orgDTO.setOrgCode(org.getOrgCode());
        orgDTO.setName(org.getName());
        orgDTO.setLeaderName(org.getLeaderName());
        orgDTO.setSortOrder(org.getSortOrder());
        orgDTO.setStatus(org.getStatus());
        orgDTO.setCreateTime(org.getCreateTime());
        orgDTO.setUpdateTime(org.getUpdateTime());
        return orgDTO;
    }
}

