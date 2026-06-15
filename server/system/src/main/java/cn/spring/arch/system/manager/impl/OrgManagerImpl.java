package cn.spring.arch.system.manager.impl;

import cn.spring.arch.common.page.PageReqParam;
import cn.spring.arch.system.entity.Org;
import cn.spring.arch.system.manager.OrgManager;
import cn.spring.arch.system.mapper.OrgMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class OrgManagerImpl implements OrgManager {

    @Resource
    private OrgMapper orgMapper;

    @Override
    public List<Org> listAll() {
        return orgMapper.selectList(new LambdaQueryWrapper<Org>()
                .orderByAsc(Org::getSortOrder)
                .orderByAsc(Org::getId));
    }

    @Override
    public Page<Org> page(PageReqParam reqParam) {
        return orgMapper.selectPage(
                new Page<Org>(reqParam.getPageNum(), reqParam.getPageSize()),
                new LambdaQueryWrapper<Org>()
                        .orderByAsc(Org::getSortOrder)
                        .orderByAsc(Org::getId)
        );
    }

    @Override
    public Org getById(Long orgId) {
        return orgMapper.selectById(orgId);
    }

    @Override
    public Org save(Org org) {
        if (org.getId() == null) {
            orgMapper.insert(org);
            return org;
        }
        orgMapper.updateById(org);
        return orgMapper.selectById(org.getId());
    }

    @Override
    public boolean existsChildren(Long orgId) {
        if (orgId == null) {
            return false;
        }
        Long count = orgMapper.selectCount(new LambdaQueryWrapper<Org>().eq(Org::getParentId, orgId));
        return count != null && count > 0;
    }

    @Override
    public void deleteById(Long orgId) {
        orgMapper.deleteById(orgId);
    }
}

