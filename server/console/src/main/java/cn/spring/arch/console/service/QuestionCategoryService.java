package cn.spring.arch.console.service;

import cn.spring.arch.common.page.PageData;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.question.CreateQuestionCategoryReqParam;
import cn.spring.arch.console.pojo.req.question.DeleteQuestionCategoryReqParam;
import cn.spring.arch.console.pojo.req.question.GrantQuestionCategoryReqParam;
import cn.spring.arch.console.pojo.req.question.ListQuestionCategoryReqParam;
import cn.spring.arch.console.pojo.req.question.UpdateQuestionCategoryReqParam;
import cn.spring.arch.console.pojo.resp.question.QuestionCategoryDTO;
import cn.spring.arch.console.pojo.resp.question.QuestionCategoryGrantDTO;

import java.util.List;

public interface QuestionCategoryService {

    RespInfo<Void> createQuestionCategory(CreateQuestionCategoryReqParam reqParam);

    RespInfo<Void> updateQuestionCategory(UpdateQuestionCategoryReqParam reqParam);

    RespInfo<Void> deleteQuestionCategory(DeleteQuestionCategoryReqParam reqParam);

    RespInfo<QuestionCategoryDTO> getQuestionCategoryById(Long id);

    RespInfo<PageData<QuestionCategoryDTO>> listQuestionCategory(ListQuestionCategoryReqParam reqParam);

    RespInfo<List<QuestionCategoryDTO>> listAllQuestionCategory();

    RespInfo<QuestionCategoryGrantDTO> getQuestionCategoryGrantByUserId(Long userId);

    RespInfo<Void> grantQuestionCategory(GrantQuestionCategoryReqParam reqParam);
}

