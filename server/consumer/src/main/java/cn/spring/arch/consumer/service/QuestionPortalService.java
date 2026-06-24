package cn.spring.arch.consumer.service;

import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.consumer.pojo.req.SubmitQuestionAnswerReqParam;
import cn.spring.arch.consumer.pojo.req.UserLoginReqParam;
import cn.spring.arch.consumer.pojo.resp.QuestionAnswerRecordDTO;
import cn.spring.arch.consumer.pojo.resp.QuestionCategoryDTO;
import cn.spring.arch.consumer.pojo.resp.QuestionDTO;
import cn.spring.arch.consumer.pojo.resp.UserLoginDTO;

import java.util.List;

public interface QuestionPortalService {

    RespInfo<UserLoginDTO> login(UserLoginReqParam reqParam);

    RespInfo<Void> logout();

    RespInfo<List<QuestionCategoryDTO>> listMyQuestionCategory();

    RespInfo<List<QuestionDTO>> listQuestionByCategoryId(Long categoryId);

    RespInfo<QuestionAnswerRecordDTO> submitQuestionAnswer(SubmitQuestionAnswerReqParam reqParam);

    RespInfo<QuestionAnswerRecordDTO> getQuestionAnswerRecordById(Long recordId);
}

