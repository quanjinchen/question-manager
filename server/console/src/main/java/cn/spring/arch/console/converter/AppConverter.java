package cn.spring.arch.console.converter;

import cn.spring.arch.common.converter.BaseConverter;
import cn.spring.arch.console.pojo.req.ListAppReqParam;
import cn.spring.arch.console.pojo.resp.AppDTO;
import cn.spring.arch.system.entity.App;
import cn.spring.arch.system.pojo.query.ListAppQuery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AppConverter extends BaseConverter {

    AppConverter INSTANCE = Mappers.getMapper(AppConverter.class);

    AppDTO convert(App app);

    List<AppDTO> convert(List<App> appList);

    ListAppQuery convert(ListAppReqParam reqParam);
}

