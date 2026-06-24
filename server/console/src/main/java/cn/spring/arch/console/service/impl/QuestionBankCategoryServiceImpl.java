package cn.spring.arch.console.service.impl;

import cn.spring.arch.common.constant.ResultCode;
import cn.spring.arch.common.page.PageData;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.question.CreateQuestionBankCategoryReqParam;
import cn.spring.arch.console.pojo.req.question.DeleteQuestionBankCategoryReqParam;
import cn.spring.arch.console.pojo.req.question.ListQuestionBankCategoryReqParam;
import cn.spring.arch.console.pojo.req.question.UpdateQuestionBankCategoryReqParam;
import cn.spring.arch.console.pojo.resp.question.QuestionBankCategoryDTO;
import cn.spring.arch.console.service.QuestionBankCategoryService;
import cn.spring.arch.system.entity.QuestionBankCategory;
import cn.spring.arch.system.manager.QuestionBankCategoryManager;
import cn.spring.arch.system.manager.QuestionCategoryManager;
import cn.spring.arch.system.pojo.query.ListQuestionBankCategoryQuery;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class QuestionBankCategoryServiceImpl implements QuestionBankCategoryService {

    @Resource
    private QuestionBankCategoryManager questionBankCategoryManager;
    @Resource
    private QuestionCategoryManager questionCategoryManager;

    @Override
    public RespInfo<Void> createQuestionBankCategory(CreateQuestionBankCategoryReqParam reqParam) {
        QuestionBankCategory sameNameCategory = questionBankCategoryManager.getByName(reqParam.getCategoryName());
        ResultCode.BAD_REQUEST.assertIsFalse(sameNameCategory != null, "题库分类名称已存在");

        QuestionBankCategory category = new QuestionBankCategory();
        category.setCategoryName(reqParam.getCategoryName());
        category.setDescription(reqParam.getDescription());
        category.setSortOrder(reqParam.getSortOrder() == null ? 0 : reqParam.getSortOrder());
        category.setStatus(reqParam.getStatus() == null ? 1 : reqParam.getStatus());
        questionBankCategoryManager.save(category);
        return RespInfo.success();
    }

    @Override
    public RespInfo<Void> updateQuestionBankCategory(UpdateQuestionBankCategoryReqParam reqParam) {
        QuestionBankCategory category = questionBankCategoryManager.getById(reqParam.getId());
        ResultCode.BAD_REQUEST.assertNotNull(category, "题库分类不存在");

        QuestionBankCategory sameNameCategory = questionBankCategoryManager.getByName(reqParam.getCategoryName());
        ResultCode.BAD_REQUEST.assertIsFalse(sameNameCategory != null && !Objects.equals(sameNameCategory.getId(), reqParam.getId()), "题库分类名称已存在");

        category.setCategoryName(reqParam.getCategoryName());
        category.setDescription(reqParam.getDescription());
        category.setSortOrder(reqParam.getSortOrder() == null ? 0 : reqParam.getSortOrder());
        category.setStatus(reqParam.getStatus() == null ? 1 : reqParam.getStatus());
        questionBankCategoryManager.save(category);
        return RespInfo.success();
    }

    @Override
    public RespInfo<Void> deleteQuestionBankCategory(DeleteQuestionBankCategoryReqParam reqParam) {
        QuestionBankCategory category = questionBankCategoryManager.getById(reqParam.getCategoryId());
        ResultCode.BAD_REQUEST.assertNotNull(category, "题库分类不存在");
        ResultCode.DATA_IN_USE.assertIsTrue(questionCategoryManager.countByBankCategoryId(reqParam.getCategoryId()) == 0, "题库分类下存在题库，无法删除");
        questionBankCategoryManager.deleteById(reqParam.getCategoryId());
        return RespInfo.success();
    }

    @Override
    public RespInfo<QuestionBankCategoryDTO> getQuestionBankCategoryById(Long id) {
        QuestionBankCategory category = questionBankCategoryManager.getById(id);
        ResultCode.BAD_REQUEST.assertNotNull(category, "题库分类不存在");
        return RespInfo.success(convertCategory(category));
    }

    @Override
    public RespInfo<PageData<QuestionBankCategoryDTO>> listQuestionBankCategory(ListQuestionBankCategoryReqParam reqParam) {
        PageHelper.startPage(reqParam.getPageNum(), reqParam.getPageSize());
        ListQuestionBankCategoryQuery query = new ListQuestionBankCategoryQuery();
        query.setCategoryName(reqParam.getCategoryName());
        query.setStatus(reqParam.getStatus());
        List<QuestionBankCategory> categories = questionBankCategoryManager.listQuestionBankCategory(query);

        PageData<QuestionBankCategoryDTO> pageData = new PageData<QuestionBankCategoryDTO>();
        pageData.setPageNum(reqParam.getPageNum());
        pageData.setPageSize(reqParam.getPageSize());
        pageData.setTotal(new PageInfo<QuestionBankCategory>(categories).getTotal());
        pageData.setRecords(categories.stream().map(this::convertCategory).collect(Collectors.toList()));
        return RespInfo.success(pageData);
    }

    @Override
    public RespInfo<List<QuestionBankCategoryDTO>> listAllQuestionBankCategory() {
        List<QuestionBankCategory> categories = questionBankCategoryManager.listQuestionBankCategory(new ListQuestionBankCategoryQuery());
        return RespInfo.success(categories.stream().map(this::convertCategory).collect(Collectors.toList()));
    }

    private QuestionBankCategoryDTO convertCategory(QuestionBankCategory category) {
        QuestionBankCategoryDTO dto = new QuestionBankCategoryDTO();
        dto.setId(category.getId());
        dto.setCategoryName(category.getCategoryName());
        dto.setDescription(category.getDescription());
        dto.setSortOrder(category.getSortOrder());
        dto.setStatus(category.getStatus());
        dto.setCreateTime(category.getCreateTime());
        dto.setUpdateTime(category.getUpdateTime());
        dto.setBankCount(questionCategoryManager.countByBankCategoryId(category.getId()));
        return dto;
    }
}
