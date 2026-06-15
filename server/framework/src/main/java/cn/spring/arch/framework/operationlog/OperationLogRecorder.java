package cn.spring.arch.framework.operationlog;

import cn.spring.arch.system.entity.OperationLog;
import cn.spring.arch.system.manager.OperationLogManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class OperationLogRecorder {

    @Resource
    private OperationLogManager operationLogManager;

    public void record(OperationLog operationLog) {
        operationLogManager.save(operationLog);
    }
}

