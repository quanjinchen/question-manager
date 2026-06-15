package cn.spring.arch.console.service;

import cn.spring.arch.common.page.PageData;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.ListOperationLogReqParam;
import cn.spring.arch.console.pojo.resp.OperationLogDTO;

public interface OperationLogService {

    RespInfo<PageData<OperationLogDTO>> listOperationLog(ListOperationLogReqParam reqParam);
}

