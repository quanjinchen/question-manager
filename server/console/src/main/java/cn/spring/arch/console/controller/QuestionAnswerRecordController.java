package cn.spring.arch.console.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.spring.arch.common.page.PageData;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.question.ListQuestionAnswerRecordReqParam;
import cn.spring.arch.console.pojo.resp.question.QuestionAnswerRecordDTO;
import cn.spring.arch.console.service.QuestionAnswerRecordService;
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

@Tag(name = "答题记录管理")
@RestController
@RequestMapping("/question-answer-record")
public class QuestionAnswerRecordController {

    @Resource
    private QuestionAnswerRecordService questionAnswerRecordService;

    @Operation(summary = "分页查询答题记录", description = "权限：question:record:query")
    @PostMapping("/list-question-answer-record")
    @SaCheckPermission("question:record:query")
    public RespInfo<PageData<QuestionAnswerRecordDTO>> listQuestionAnswerRecord(@Valid @RequestBody ListQuestionAnswerRecordReqParam reqParam) {
        return questionAnswerRecordService.listQuestionAnswerRecord(reqParam);
    }

    @Operation(summary = "根据 ID 查询答题记录", description = "权限：question:record:query")
    @GetMapping("/get-question-answer-record-by-id/{id}")
    @SaCheckPermission("question:record:query")
    public RespInfo<QuestionAnswerRecordDTO> getQuestionAnswerRecordById(@Parameter(description = "答题记录 ID") @PathVariable Long id) {
        return questionAnswerRecordService.getQuestionAnswerRecordById(id);
    }
}

