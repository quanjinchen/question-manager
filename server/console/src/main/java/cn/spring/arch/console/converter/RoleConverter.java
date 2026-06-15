package cn.spring.arch.console.converter;

import cn.spring.arch.common.converter.BaseConverter;
import cn.spring.arch.console.pojo.req.ListRoleReqParam;
import cn.spring.arch.console.pojo.resp.RoleDTO;
import cn.spring.arch.system.entity.SysRole;
import cn.spring.arch.system.pojo.query.ListRoleQuery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RoleConverter extends BaseConverter {

    RoleConverter INSTANCE = Mappers.getMapper(RoleConverter.class);

    RoleDTO convert(SysRole role);

    List<RoleDTO> convert(List<SysRole> roles);

    ListRoleQuery convert(ListRoleReqParam reqParam);
}

