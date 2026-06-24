package cn.spring.arch.consumer.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.consumer.pojo.req.SubmitQuestionAnswerReqParam;
import cn.spring.arch.consumer.pojo.req.UserLoginReqParam;
import cn.spring.arch.consumer.pojo.resp.QuestionAnswerRecordDTO;
import cn.spring.arch.consumer.pojo.resp.QuestionCategoryDTO;
import cn.spring.arch.consumer.pojo.resp.QuestionDTO;
import cn.spring.arch.consumer.pojo.resp.UserLoginDTO;
import cn.spring.arch.consumer.service.QuestionPortalService;
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
import java.util.List;

@Tag(name = "用户答题端")
@RestController
@RequestMapping("/question-portal")
public class QuestionPortalController {

    @Resource
    private QuestionPortalService questionPortalService;

    @Operation(summary = "用户登录")
    @SaIgnore
    @PostMapping("/login")
    public RespInfo<UserLoginDTO> login(@Valid @RequestBody UserLoginReqParam reqParam) {
        return questionPortalService.login(reqParam);
    }

    @Operation(summary = "用户退出")
    @PostMapping("/logout")
    public RespInfo<Void> logout() {
        return questionPortalService.logout();
    }

    @Operation(summary = "查询我的授权题目分类")
    @PostMapping("/list-my-question-category")
    public RespInfo<List<QuestionCategoryDTO>> listMyQuestionCategory() {
        return questionPortalService.listMyQuestionCategory();
    }

    @Operation(summary = "查询分类下可答题目")
    @GetMapping("/list-question-by-category-id/{id}")
    public RespInfo<List<QuestionDTO>> listQuestionByCategoryId(@Parameter(description = "分类 ID") @PathVariable Long id) {
        return questionPortalService.listQuestionByCategoryId(id);
    }

    @Operation(summary = "提交答卷")
    @PostMapping("/submit-question-answer")
    public RespInfo<QuestionAnswerRecordDTO> submitQuestionAnswer(@Valid @RequestBody SubmitQuestionAnswerReqParam reqParam) {
        return questionPortalService.submitQuestionAnswer(reqParam);
    }

    @Operation(summary = "查询用户答题成绩")
    @GetMapping("/get-question-answer-record-by-id/{id}")
    public RespInfo<QuestionAnswerRecordDTO> getQuestionAnswerRecordById(@Parameter(description = "答题记录 ID") @PathVariable Long id) {
        return questionPortalService.getQuestionAnswerRecordById(id);
    }
}
