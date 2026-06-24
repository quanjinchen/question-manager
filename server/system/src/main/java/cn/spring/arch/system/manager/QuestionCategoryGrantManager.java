package cn.spring.arch.system.manager;

import cn.spring.arch.system.entity.QuestionCategoryGrant;

import java.util.Collection;
import java.util.List;

public interface QuestionCategoryGrantManager {

    QuestionCategoryGrant getByUserIdAndCategoryId(Long userId, Long categoryId);

    List<QuestionCategoryGrant> listByUserId(Long userId);

    List<QuestionCategoryGrant> listByCategoryId(Long categoryId);

    List<QuestionCategoryGrant> listByUserIds(Collection<Long> userIds);

    void deleteByUserId(Long userId);

    void saveBatch(Long userId, List<Long> categoryIds);
}

