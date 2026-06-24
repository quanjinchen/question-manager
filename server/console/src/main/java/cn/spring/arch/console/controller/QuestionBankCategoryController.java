package cn.spring.arch.console.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.spring.arch.common.page.PageData;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.question.CreateQuestionBankCategoryReqParam;
import cn.spring.arch.console.pojo.req.question.DeleteQuestionBankCategoryReqParam;
import cn.spring.arch.console.pojo.req.question.ListQuestionBankCategoryReqParam;
import cn.spring.arch.console.pojo.req.question.UpdateQuestionBankCategoryReqParam;
import cn.spring.arch.console.pojo.resp.question.QuestionBankCategoryDTO;
import cn.spring.arch.console.service.QuestionBankCategoryService;
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

@Tag(name = "题库分类管理")
@RestController
@RequestMapping("/question-bank-category")
public class QuestionBankCategoryController {

    @Resource
    private QuestionBankCategoryService questionBankCategoryService;

    @Operation(summary = "创建题库分类", description = "权限：question:bankCategory:update")
    @OperateLog(module = "题库分类管理", action = "创建题库分类")
    @PostMapping("/create-question-bank-category")
    @SaCheckPermission("question:bankCategory:update")
    public RespInfo<Void> createQuestionBankCategory(@Valid @RequestBody CreateQuestionBankCategoryReqParam reqParam) {
        return questionBankCategoryService.createQuestionBankCategory(reqParam);
    }

    @Operation(summary = "更新题库分类", description = "权限：question:bankCategory:update")
    @OperateLog(module = "题库分类管理", action = "更新题库分类")
    @PostMapping("/update-question-bank-category")
    @SaCheckPermission("question:bankCategory:update")
    public RespInfo<Void> updateQuestionBankCategory(@Valid @RequestBody UpdateQuestionBankCategoryReqParam reqParam) {
        return questionBankCategoryService.updateQuestionBankCategory(reqParam);
    }

    @Operation(summary = "删除题库分类", description = "权限：question:bankCategory:delete")
    @OperateLog(module = "题库分类管理", action = "删除题库分类")
    @PostMapping("/delete-question-bank-category")
    @SaCheckPermission("question:bankCategory:delete")
    public RespInfo<Void> deleteQuestionBankCategory(@Valid @RequestBody DeleteQuestionBankCategoryReqParam reqParam) {
        return questionBankCategoryService.deleteQuestionBankCategory(reqParam);
    }

    @Operation(summary = "根据 ID 查询题库分类", description = "权限：question:bankCategory:query")
    @GetMapping("/get-question-bank-category-by-id/{id}")
    @SaCheckPermission("question:bankCategory:query")
    public RespInfo<QuestionBankCategoryDTO> getQuestionBankCategoryById(@Parameter(description = "题库分类 ID") @PathVariable Long id) {
        return questionBankCategoryService.getQuestionBankCategoryById(id);
    }

    @Operation(summary = "分页查询题库分类", description = "权限：question:bankCategory:query")
    @PostMapping("/list-question-bank-category")
    @SaCheckPermission("question:bankCategory:query")
    public RespInfo<PageData<QuestionBankCategoryDTO>> listQuestionBankCategory(@Valid @RequestBody ListQuestionBankCategoryReqParam reqParam) {
        return questionBankCategoryService.listQuestionBankCategory(reqParam);
    }

    @Operation(summary = "查询全部题库分类", description = "权限：question:bankCategory:query")
    @PostMapping("/list-all-question-bank-category")
    @SaCheckPermission("question:bankCategory:query")
    public RespInfo<List<QuestionBankCategoryDTO>> listAllQuestionBankCategory() {
        return questionBankCategoryService.listAllQuestionBankCategory();
    }
}
