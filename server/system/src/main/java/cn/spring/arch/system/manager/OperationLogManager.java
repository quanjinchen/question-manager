package cn.spring.arch.system.manager;

import cn.spring.arch.common.page.PageReqParam;
import cn.spring.arch.system.entity.OperationLog;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface OperationLogManager {

    Page<OperationLog> page(PageReqParam reqParam);

    List<OperationLog> listOperationLogs();

    void save(OperationLog operationLog);
}

