package cn.spring.arch.console.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.spring.arch.common.page.PageData;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.ListOperationLogReqParam;
import cn.spring.arch.console.pojo.resp.OperationLogDTO;
import cn.spring.arch.console.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "操作日志")
@RestController
@RequestMapping("/operation-log")
public class OperationLogController {

    private final OperationLogService operationLogService;

    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @Operation(summary = "分页查询操作日志", description = "权限：system:operationLog:query")
    @PostMapping("/list-operation-log")
    @SaCheckPermission("system:operationLog:query")
    public RespInfo<PageData<OperationLogDTO>> listOperationLog(@Valid @RequestBody ListOperationLogReqParam reqParam) {
        return operationLogService.listOperationLog(reqParam);
    }
}

