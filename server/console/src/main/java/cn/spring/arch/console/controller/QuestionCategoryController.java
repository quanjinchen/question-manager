package cn.spring.arch.console.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.spring.arch.common.page.PageData;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.question.CreateQuestionCategoryReqParam;
import cn.spring.arch.console.pojo.req.question.DeleteQuestionCategoryReqParam;
import cn.spring.arch.console.pojo.req.question.GrantQuestionCategoryReqParam;
import cn.spring.arch.console.pojo.req.question.ListQuestionCategoryReqParam;
import cn.spring.arch.console.pojo.req.question.UpdateQuestionCategoryReqParam;
import cn.spring.arch.console.pojo.resp.question.QuestionCategoryDTO;
import cn.spring.arch.console.pojo.resp.question.QuestionCategoryGrantDTO;
import cn.spring.arch.console.service.QuestionCategoryService;
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
import java.util.List;

@Tag(name = "题目分类管理")
@RestController
@RequestMapping("/question-category")
public class QuestionCategoryController {

    @Resource
    private QuestionCategoryService questionCategoryService;

    @Operation(summary = "创建题目分类", description = "权限：question:category:update")
    @OperateLog(module = "题目分类管理", action = "创建题目分类")
    @PostMapping("/create-question-category")
    @SaCheckPermission("question:category:update")
    public RespInfo<Void> createQuestionCategory(@Valid @RequestBody CreateQuestionCategoryReqParam reqParam) {
        return questionCategoryService.createQuestionCategory(reqParam);
    }

    @Operation(summary = "更新题目分类", description = "权限：question:category:update")
    @OperateLog(module = "题目分类管理", action = "更新题目分类")
    @PostMapping("/update-question-category")
    @SaCheckPermission("question:category:update")
    public RespInfo<Void> updateQuestionCategory(@Valid @RequestBody UpdateQuestionCategoryReqParam reqParam) {
        return questionCategoryService.updateQuestionCategory(reqParam);
    }

    @Operation(summary = "删除题目分类", description = "权限：question:category:delete")
    @OperateLog(module = "题目分类管理", action = "删除题目分类")
    @PostMapping("/delete-question-category")
    @SaCheckPermission("question:category:delete")
    public RespInfo<Void> deleteQuestionCategory(@Valid @RequestBody DeleteQuestionCategoryReqParam reqParam) {
        return questionCategoryService.deleteQuestionCategory(reqParam);
    }

    @Operation(summary = "根据 ID 查询题目分类", description = "权限：question:category:query")
    @GetMapping("/get-question-category-by-id/{id}")
    @SaCheckPermission("question:category:query")
    public RespInfo<QuestionCategoryDTO> getQuestionCategoryById(@Parameter(description = "分类 ID") @PathVariable Long id) {
        return questionCategoryService.getQuestionCategoryById(id);
    }

    @Operation(summary = "分页查询题目分类", description = "权限：question:category:query")
    @PostMapping("/list-question-category")
    @SaCheckPermission("question:category:query")
    public RespInfo<PageData<QuestionCategoryDTO>> listQuestionCategory(@Valid @RequestBody ListQuestionCategoryReqParam reqParam) {
        return questionCategoryService.listQuestionCategory(reqParam);
    }

    @Operation(summary = "查询全部题目分类", description = "权限：question:category:query")
    @PostMapping("/list-all-question-category")
    @SaCheckPermission("question:category:query")
    public RespInfo<List<QuestionCategoryDTO>> listAllQuestionCategory() {
        return questionCategoryService.listAllQuestionCategory();
    }

    @Operation(summary = "查询用户题目分类授权", description = "权限：question:category:grant")
    @GetMapping("/get-question-category-grant-by-user-id/{id}")
    @SaCheckPermission("question:category:grant")
    public RespInfo<QuestionCategoryGrantDTO> getQuestionCategoryGrantByUserId(@Parameter(description = "用户 ID") @PathVariable Long id) {
        return questionCategoryService.getQuestionCategoryGrantByUserId(id);
    }

    @Operation(summary = "授权用户题目分类", description = "权限：question:category:grant")
    @OperateLog(module = "题目分类管理", action = "授权题目分类")
    @PostMapping("/grant-question-category")
    @SaCheckPermission("question:category:grant")
    public RespInfo<Void> grantQuestionCategory(@Valid @RequestBody GrantQuestionCategoryReqParam reqParam) {
        return questionCategoryService.grantQuestionCategory(reqParam);
    }
}

