package cn.spring.arch.system.manager;

import cn.spring.arch.system.entity.QuestionAnswerRecord;
import cn.spring.arch.system.pojo.query.ListQuestionAnswerRecordQuery;

import java.util.List;

public interface QuestionAnswerRecordManager {

    QuestionAnswerRecord getById(Long id);

    List<QuestionAnswerRecord> listQuestionAnswerRecord(ListQuestionAnswerRecordQuery query);

    QuestionAnswerRecord save(QuestionAnswerRecord record);
}

