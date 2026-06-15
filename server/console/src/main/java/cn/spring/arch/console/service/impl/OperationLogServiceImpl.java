package cn.spring.arch.console.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import cn.spring.arch.common.page.PageData;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.ListOperationLogReqParam;
import cn.spring.arch.console.pojo.resp.OperationLogDTO;
import cn.spring.arch.console.service.OperationLogService;
import cn.spring.arch.system.entity.OperationLog;
import cn.spring.arch.system.manager.OperationLogManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Resource
    private OperationLogManager operationLogManager;

    @Override
    public RespInfo<PageData<OperationLogDTO>> listOperationLog(ListOperationLogReqParam reqParam) {
        PageHelper.startPage(reqParam.getPageNum(), reqParam.getPageSize());
        List<OperationLog> operationLogList = operationLogManager.listOperationLogs();

        List<OperationLogDTO> operationLogDTOList = new ArrayList<OperationLogDTO>(operationLogList.size());
        for (OperationLog operationLog : operationLogList) {
            OperationLogDTO operationLogDTO = new OperationLogDTO();
            operationLogDTO.setId(operationLog.getId());
            operationLogDTO.setModuleName(operationLog.getModuleName());
            operationLogDTO.setActionName(operationLog.getActionName());
            operationLogDTO.setOperatorName(operationLog.getOperatorName());
            operationLogDTO.setRequestPath(operationLog.getRequestPath());
            operationLogDTO.setSuccessFlag(operationLog.getSuccessFlag());
            operationLogDTO.setRequestTime(operationLog.getRequestTime());
            operationLogDTOList.add(operationLogDTO);
        }

        PageData<OperationLogDTO> pageData = new PageData<OperationLogDTO>();
        pageData.setTotal(new PageInfo<OperationLog>(operationLogList).getTotal());
        pageData.setRecords(operationLogDTOList);
        pageData.setPageNum(reqParam.getPageNum());
        pageData.setPageSize(reqParam.getPageSize());
        return RespInfo.success(pageData);
    }
}

