package cn.spring.arch.system.manager.impl;

import cn.spring.arch.common.page.PageReqParam;
import cn.spring.arch.system.entity.OperationLog;
import cn.spring.arch.system.manager.OperationLogManager;
import cn.spring.arch.system.mapper.OperationLogMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class OperationLogManagerImpl implements OperationLogManager {

    @Resource
    private OperationLogMapper operationLogMapper;

    @Override
    public Page<OperationLog> page(PageReqParam reqParam) {
        return operationLogMapper.selectPage(
                new Page<OperationLog>(reqParam.getPageNum(), reqParam.getPageSize()),
                new LambdaQueryWrapper<OperationLog>()
                        .orderByDesc(OperationLog::getRequestTime)
                        .orderByDesc(OperationLog::getId)
        );
    }

    @Override
    public List<OperationLog> listOperationLogs() {
        return operationLogMapper.selectList(new LambdaQueryWrapper<OperationLog>()
                .orderByDesc(OperationLog::getRequestTime)
                .orderByDesc(OperationLog::getId));
    }

    @Override
    public void save(OperationLog operationLog) {
        operationLogMapper.insert(operationLog);
    }
}

