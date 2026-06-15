package cn.spring.arch.system.manager;

import cn.spring.arch.system.entity.OrgUser;

import java.util.List;

public interface OrgUserManager {

    List<OrgUser> listByOrgId(Long orgId);

    List<OrgUser> listByUserId(Long userId);

    boolean existsByUserId(Long userId);

    boolean existsByOrgId(Long orgId);

    void replaceOrgUsers(Long orgId, List<Long> userIds);

    void save(OrgUser orgUser);

    void deleteByUserId(Long userId);

    void deleteByOrgId(Long orgId);
}

