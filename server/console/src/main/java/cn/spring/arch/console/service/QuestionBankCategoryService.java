package cn.spring.arch.console.service;

import cn.spring.arch.common.page.PageData;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.question.CreateQuestionBankCategoryReqParam;
import cn.spring.arch.console.pojo.req.question.DeleteQuestionBankCategoryReqParam;
import cn.spring.arch.console.pojo.req.question.ListQuestionBankCategoryReqParam;
import cn.spring.arch.console.pojo.req.question.UpdateQuestionBankCategoryReqParam;
import cn.spring.arch.console.pojo.resp.question.QuestionBankCategoryDTO;

import java.util.List;

public interface QuestionBankCategoryService {

    RespInfo<Void> createQuestionBankCategory(CreateQuestionBankCategoryReqParam reqParam);

    RespInfo<Void> updateQuestionBankCategory(UpdateQuestionBankCategoryReqParam reqParam);

    RespInfo<Void> deleteQuestionBankCategory(DeleteQuestionBankCategoryReqParam reqParam);

    RespInfo<QuestionBankCategoryDTO> getQuestionBankCategoryById(Long id);

    RespInfo<PageData<QuestionBankCategoryDTO>> listQuestionBankCategory(ListQuestionBankCategoryReqParam reqParam);

    RespInfo<List<QuestionBankCategoryDTO>> listAllQuestionBankCategory();
}
