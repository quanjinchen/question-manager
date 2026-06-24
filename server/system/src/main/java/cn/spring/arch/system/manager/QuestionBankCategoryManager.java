package cn.spring.arch.system.manager;

import cn.spring.arch.system.entity.QuestionBankCategory;
import cn.spring.arch.system.pojo.query.ListQuestionBankCategoryQuery;

import java.util.Collection;
import java.util.List;

public interface QuestionBankCategoryManager {

    QuestionBankCategory getById(Long id);

    QuestionBankCategory getByName(String categoryName);

    List<QuestionBankCategory> listQuestionBankCategory(ListQuestionBankCategoryQuery query);

    List<QuestionBankCategory> listByIds(Collection<Long> ids);

    QuestionBankCategory save(QuestionBankCategory category);

    void deleteById(Long id);
}
