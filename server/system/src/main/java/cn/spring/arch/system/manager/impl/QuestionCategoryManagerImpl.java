package cn.spring.arch.system.manager.impl;

import cn.spring.arch.system.entity.QuestionCategory;
import cn.spring.arch.system.manager.QuestionCategoryManager;
import cn.spring.arch.system.mapper.QuestionCategoryMapper;
import cn.spring.arch.system.pojo.query.ListQuestionCategoryQuery;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class QuestionCategoryManagerImpl implements QuestionCategoryManager {

    @Resource
    private QuestionCategoryMapper questionCategoryMapper;

    @Override
    public QuestionCategory getById(Long id) {
        return questionCategoryMapper.selectById(id);
    }

    @Override
    public QuestionCategory getByName(String categoryName) {
        if (!StringUtils.hasText(categoryName)) {
            return null;
        }
        return questionCategoryMapper.selectOne(new LambdaQueryWrapper<QuestionCategory>()
                .eq(QuestionCategory::getCategoryName, categoryName)
                .last("limit 1"));
    }

    @Override
    public List<QuestionCategory> listQuestionCategory(ListQuestionCategoryQuery query) {
        LambdaQueryWrapper<QuestionCategory> wrapper = new LambdaQueryWrapper<QuestionCategory>()
                .orderByAsc(QuestionCategory::getSortOrder)
                .orderByDesc(QuestionCategory::getId);
        if (query != null) {
            if (StringUtils.hasText(query.getCategoryName())) {
                wrapper.like(QuestionCategory::getCategoryName, query.getCategoryName());
            }
            if (query.getStatus() != null) {
                wrapper.eq(QuestionCategory::getStatus, query.getStatus());
            }
        }
        return questionCategoryMapper.selectList(wrapper);
    }

    @Override
    public List<QuestionCategory> listByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return questionCategoryMapper.selectBatchIds(ids);
    }

    @Override
    public QuestionCategory save(QuestionCategory category) {
        if (category.getId() == null) {
            questionCategoryMapper.insert(category);
            return category;
        }
        questionCategoryMapper.updateById(category);
        return questionCategoryMapper.selectById(category.getId());
    }

    @Override
    public void deleteById(Long id) {
        questionCategoryMapper.deleteById(id);
    }
}

