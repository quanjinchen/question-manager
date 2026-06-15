package cn.spring.arch.system.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.spring.arch.system.entity.FaceAuthLog;
import cn.spring.arch.system.manager.FaceAuthLogManager;
import cn.spring.arch.system.mapper.FaceAuthLogMapper;
import cn.spring.arch.system.pojo.query.ListFaceAuthLogQuery;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
public class FaceAuthLogManagerImpl implements FaceAuthLogManager {

    @Resource
    private FaceAuthLogMapper faceAuthLogMapper;

    @Override
    public void save(FaceAuthLog faceAuthLog) {
        faceAuthLogMapper.insert(faceAuthLog);
    }

    @Override
    public List<FaceAuthLog> listFaceAuthLogs(ListFaceAuthLogQuery query) {
        LambdaQueryWrapper<FaceAuthLog> queryWrapper = new LambdaQueryWrapper<FaceAuthLog>()
                .orderByDesc(FaceAuthLog::getCreateTime)
                .orderByDesc(FaceAuthLog::getId);
        if (query == null) {
            return faceAuthLogMapper.selectList(queryWrapper);
        }
        if (query.getAuthApiType() != null) {
            queryWrapper.eq(FaceAuthLog::getAuthApiType, query.getAuthApiType());
        }
        if (StringUtils.hasText(query.getIp())) {
            queryWrapper.like(FaceAuthLog::getIp, query.getIp());
        }
        if (query.getStatus() != null) {
            queryWrapper.eq(FaceAuthLog::getStatus, query.getStatus());
        }
        if (StringUtils.hasText(query.getAppName())) {
            queryWrapper.like(FaceAuthLog::getAppName, query.getAppName());
        }
        if (StringUtils.hasText(query.getAuthFullName())) {
            queryWrapper.like(FaceAuthLog::getAuthFullName, query.getAuthFullName());
        }
        return faceAuthLogMapper.selectList(queryWrapper);
    }
}

