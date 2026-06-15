package cn.spring.arch.console.converter;

import cn.spring.arch.common.converter.BaseConverter;
import cn.spring.arch.console.pojo.req.ListUserReqParam;
import cn.spring.arch.console.pojo.resp.UserDTO;
import cn.spring.arch.system.entity.User;
import cn.spring.arch.system.pojo.query.ListUserQuery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserConverter extends BaseConverter {

    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    @Mapping(target = "phone", source = "phone", qualifiedByName = TO_PLAIN_TEXT)
    @Mapping(target = "idCard", source = "idCard", qualifiedByName = TO_PLAIN_TEXT)
    UserDTO convert(User user);

    List<UserDTO> convert(List<User> users);

    @Mapping(target = "phone", source = "phone", qualifiedByName = TO_ENCRYPT_FIELD)
    @Mapping(target = "idCard", source = "idCard", qualifiedByName = TO_ENCRYPT_FIELD)
    User convert(UserDTO userDTO);

    ListUserQuery convert(ListUserReqParam reqParam);
}

