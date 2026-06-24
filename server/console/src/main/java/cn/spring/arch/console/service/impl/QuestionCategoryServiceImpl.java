package cn.spring.arch.console.service.impl;

import cn.spring.arch.common.constant.ResultCode;
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
import cn.spring.arch.system.entity.QuestionCategory;
import cn.spring.arch.system.entity.QuestionCategoryGrant;
import cn.spring.arch.system.entity.User;
import cn.spring.arch.system.manager.QuestionCategoryGrantManager;
import cn.spring.arch.system.manager.QuestionCategoryManager;
import cn.spring.arch.system.manager.QuestionManager;
import cn.spring.arch.system.manager.UserManager;
import cn.spring.arch.system.pojo.query.ListQuestionCategoryQuery;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class QuestionCategoryServiceImpl implements QuestionCategoryService {

    @Resource
    private QuestionCategoryManager questionCategoryManager;
    @Resource
    private QuestionManager questionManager;
    @Resource
    private QuestionCategoryGrantManager questionCategoryGrantManager;
    @Resource
    private UserManager userManager;

    @Override
    public RespInfo<Void> createQuestionCategory(CreateQuestionCategoryReqParam reqParam) {
        QuestionCategory sameNameCategory = questionCategoryManager.getByName(reqParam.getCategoryName());
        ResultCode.BAD_REQUEST.assertIsFalse(sameNameCategory != null, "题目分类名称已存在");

        QuestionCategory category = new QuestionCategory();
        category.setCategoryName(reqParam.getCategoryName());
        category.setDescription(reqParam.getDescription());
        category.setSortOrder(reqParam.getSortOrder() == null ? 0 : reqParam.getSortOrder());
        category.setStatus(reqParam.getStatus() == null ? 1 : reqParam.getStatus());
        questionCategoryManager.save(category);
        return RespInfo.success();
    }

    @Override
    public RespInfo<Void> updateQuestionCategory(UpdateQuestionCategoryReqParam reqParam) {
        QuestionCategory category = questionCategoryManager.getById(reqParam.getId());
        ResultCode.BAD_REQUEST.assertNotNull(category, "题目分类不存在");

        QuestionCategory sameNameCategory = questionCategoryManager.getByName(reqParam.getCategoryName());
        ResultCode.BAD_REQUEST.assertIsFalse(sameNameCategory != null && !Objects.equals(sameNameCategory.getId(), reqParam.getId()), "题目分类名称已存在");

        category.setCategoryName(reqParam.getCategoryName());
        category.setDescription(reqParam.getDescription());
        category.setSortOrder(reqParam.getSortOrder() == null ? 0 : reqParam.getSortOrder());
        category.setStatus(reqParam.getStatus() == null ? 1 : reqParam.getStatus());
        questionCategoryManager.save(category);
        return RespInfo.success();
    }

    @Override
    public RespInfo<Void> deleteQuestionCategory(DeleteQuestionCategoryReqParam reqParam) {
        QuestionCategory category = questionCategoryManager.getById(reqParam.getCategoryId());
        ResultCode.BAD_REQUEST.assertNotNull(category, "题目分类不存在");
        ResultCode.DATA_IN_USE.assertIsTrue(questionManager.countByCategoryId(reqParam.getCategoryId()) == 0, "分类下存在题目，无法删除");
        questionCategoryManager.deleteById(reqParam.getCategoryId());
        return RespInfo.success();
    }

    @Override
    public RespInfo<QuestionCategoryDTO> getQuestionCategoryById(Long id) {
        QuestionCategory category = questionCategoryManager.getById(id);
        ResultCode.BAD_REQUEST.assertNotNull(category, "题目分类不存在");
        return RespInfo.success(convertCategory(category));
    }

    @Override
    public RespInfo<PageData<QuestionCategoryDTO>> listQuestionCategory(ListQuestionCategoryReqParam reqParam) {
        PageHelper.startPage(reqParam.getPageNum(), reqParam.getPageSize());
        ListQuestionCategoryQuery query = new ListQuestionCategoryQuery();
        query.setCategoryName(reqParam.getCategoryName());
        query.setStatus(reqParam.getStatus());
        List<QuestionCategory> categories = questionCategoryManager.listQuestionCategory(query);

        PageData<QuestionCategoryDTO> pageData = new PageData<QuestionCategoryDTO>();
        pageData.setPageNum(reqParam.getPageNum());
        pageData.setPageSize(reqParam.getPageSize());
        pageData.setTotal(new PageInfo<QuestionCategory>(categories).getTotal());
        pageData.setRecords(categories.stream().map(this::convertCategory).collect(Collectors.toList()));
        return RespInfo.success(pageData);
    }

    @Override
    public RespInfo<List<QuestionCategoryDTO>> listAllQuestionCategory() {
        List<QuestionCategory> categories = questionCategoryManager.listQuestionCategory(new ListQuestionCategoryQuery());
        return RespInfo.success(categories.stream().map(this::convertCategory).collect(Collectors.toList()));
    }

    @Override
    public RespInfo<QuestionCategoryGrantDTO> getQuestionCategoryGrantByUserId(Long userId) {
        User user = userManager.getById(userId);
        ResultCode.USER_NOT_FOUND.assertNotNull(user);
        List<QuestionCategoryGrant> grants = questionCategoryGrantManager.listByUserId(userId);
        QuestionCategoryGrantDTO dto = new QuestionCategoryGrantDTO();
        dto.setUserId(userId);
        dto.setCategoryIds(grants.stream().map(QuestionCategoryGrant::getCategoryId).collect(Collectors.toList()));
        return RespInfo.success(dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RespInfo<Void> grantQuestionCategory(GrantQuestionCategoryReqParam reqParam) {
        User user = userManager.getById(reqParam.getUserId());
        ResultCode.USER_NOT_FOUND.assertNotNull(user);
        List<Long> categoryIds = reqParam.getCategoryIds() == null ? new ArrayList<Long>() : reqParam.getCategoryIds();
        if (!categoryIds.isEmpty()) {
            ResultCode.BAD_REQUEST.assertIsTrue(questionCategoryManager.listByIds(categoryIds).size() == categoryIds.size(), "存在无效题目分类");
        }
        questionCategoryGrantManager.deleteByUserId(reqParam.getUserId());
        questionCategoryGrantManager.saveBatch(reqParam.getUserId(), categoryIds);
        return RespInfo.success();
    }

    private QuestionCategoryDTO convertCategory(QuestionCategory category) {
        QuestionCategoryDTO dto = new QuestionCategoryDTO();
        dto.setId(category.getId());
        dto.setCategoryName(category.getCategoryName());
        dto.setDescription(category.getDescription());
        dto.setSortOrder(category.getSortOrder());
        dto.setStatus(category.getStatus());
        dto.setCreateTime(category.getCreateTime());
        dto.setUpdateTime(category.getUpdateTime());
        dto.setQuestionCount(questionManager.countByCategoryId(category.getId()));
        return dto;
    }
}

