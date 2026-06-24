package cn.spring.arch.console.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.spring.arch.common.page.PageData;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.question.CreateQuestionReqParam;
import cn.spring.arch.console.pojo.req.question.DeleteQuestionReqParam;
import cn.spring.arch.console.pojo.req.question.ListQuestionReqParam;
import cn.spring.arch.console.pojo.req.question.UpdateQuestionReqParam;
import cn.spring.arch.console.pojo.resp.question.QuestionDTO;
import cn.spring.arch.console.service.QuestionService;
import cn.spring.arch.framework.operationlog.annotation.OperateLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@Tag(name = "题目管理")
@RestController
@RequestMapping("/question")
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Operation(summary = "创建题目", description = "权限：question:item:update")
    @OperateLog(module = "题目管理", action = "创建题目")
    @PostMapping("/create-question")
    @SaCheckPermission("question:item:update")
    public RespInfo<Void> createQuestion(@Valid @RequestBody CreateQuestionReqParam reqParam) {
        return questionService.createQuestion(reqParam);
    }

    @Operation(summary = "更新题目", description = "权限：question:item:update")
    @OperateLog(module = "题目管理", action = "更新题目")
    @PostMapping("/update-question")
    @SaCheckPermission("question:item:update")
    public RespInfo<Void> updateQuestion(@Valid @RequestBody UpdateQuestionReqParam reqParam) {
        return questionService.updateQuestion(reqParam);
    }

    @Operation(summary = "删除题目", description = "权限：question:item:delete")
    @OperateLog(module = "题目管理", action = "删除题目")
    @PostMapping("/delete-question")
    @SaCheckPermission("question:item:delete")
    public RespInfo<Void> deleteQuestion(@Valid @RequestBody DeleteQuestionReqParam reqParam) {
        return questionService.deleteQuestion(reqParam);
    }

    @Operation(summary = "根据 ID 查询题目", description = "权限：question:item:query")
    @GetMapping("/get-question-by-id/{id}")
    @SaCheckPermission("question:item:query")
    public RespInfo<QuestionDTO> getQuestionById(@Parameter(description = "题目 ID") @PathVariable Long id) {
        return questionService.getQuestionById(id);
    }

    @Operation(summary = "分页查询题目", description = "权限：question:item:query")
    @PostMapping("/list-question")
    @SaCheckPermission("question:item:query")
    public RespInfo<PageData<QuestionDTO>> listQuestion(@Valid @RequestBody ListQuestionReqParam reqParam) {
        return questionService.listQuestion(reqParam);
    }
}

