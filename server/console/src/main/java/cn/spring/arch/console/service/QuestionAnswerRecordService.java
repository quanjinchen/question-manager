package cn.spring.arch.console.service;

import cn.spring.arch.common.page.PageData;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.question.ListQuestionAnswerRecordReqParam;
import cn.spring.arch.console.pojo.resp.question.QuestionAnswerRecordDTO;

public interface QuestionAnswerRecordService {

    RespInfo<PageData<QuestionAnswerRecordDTO>> listQuestionAnswerRecord(ListQuestionAnswerRecordReqParam reqParam);

    RespInfo<QuestionAnswerRecordDTO> getQuestionAnswerRecordById(Long id);
}

