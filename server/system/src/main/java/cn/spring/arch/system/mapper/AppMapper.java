package cn.spring.arch.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.spring.arch.system.entity.App;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppMapper extends BaseMapper<App> {
}

