package cn.spring.arch.system.manager;

import cn.spring.arch.system.entity.QuestionCategory;
import cn.spring.arch.system.pojo.query.ListQuestionCategoryQuery;

import java.util.Collection;
import java.util.List;

public interface QuestionCategoryManager {

    QuestionCategory getById(Long id);

    QuestionCategory getByName(String categoryName);

    List<QuestionCategory> listQuestionCategory(ListQuestionCategoryQuery query);

    List<QuestionCategory> listByIds(Collection<Long> ids);

    QuestionCategory save(QuestionCategory category);

    void deleteById(Long id);
}

