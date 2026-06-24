package cn.spring.arch.system.manager;

import cn.spring.arch.system.entity.QuestionAnswerDetail;

import java.util.List;

public interface QuestionAnswerDetailManager {

    List<QuestionAnswerDetail> listByRecordId(Long recordId);

    void saveBatch(List<QuestionAnswerDetail> details);
}

