package cn.spring.arch.system.manager;

import cn.spring.arch.system.entity.Question;
import cn.spring.arch.system.pojo.query.ListQuestionQuery;

import java.util.List;

public interface QuestionManager {

    Question getById(Long id);

    List<Question> listQuestion(ListQuestionQuery query);

    long countByCategoryId(Long categoryId);

    Question save(Question question);

    void deleteById(Long id);
}

