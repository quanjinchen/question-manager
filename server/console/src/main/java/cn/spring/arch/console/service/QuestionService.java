package cn.spring.arch.console.service;

import cn.spring.arch.common.page.PageData;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.question.CreateQuestionReqParam;
import cn.spring.arch.console.pojo.req.question.DeleteQuestionReqParam;
import cn.spring.arch.console.pojo.req.question.ListQuestionReqParam;
import cn.spring.arch.console.pojo.req.question.UpdateQuestionReqParam;
import cn.spring.arch.console.pojo.resp.question.QuestionDTO;

public interface QuestionService {

    RespInfo<Void> createQuestion(CreateQuestionReqParam reqParam);

    RespInfo<Void> updateQuestion(UpdateQuestionReqParam reqParam);

    RespInfo<Void> deleteQuestion(DeleteQuestionReqParam reqParam);

    RespInfo<QuestionDTO> getQuestionById(Long id);

    RespInfo<PageData<QuestionDTO>> listQuestion(ListQuestionReqParam reqParam);
}

