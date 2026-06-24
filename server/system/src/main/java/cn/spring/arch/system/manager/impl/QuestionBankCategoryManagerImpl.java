package cn.spring.arch.system.manager.impl;

import cn.spring.arch.system.entity.QuestionBankCategory;
import cn.spring.arch.system.manager.QuestionBankCategoryManager;
import cn.spring.arch.system.mapper.QuestionBankCategoryMapper;
import cn.spring.arch.system.pojo.query.ListQuestionBankCategoryQuery;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class QuestionBankCategoryManagerImpl implements QuestionBankCategoryManager {

    @Resource
    private QuestionBankCategoryMapper questionBankCategoryMapper;

    @Override
    public QuestionBankCategory getById(Long id) {
        return questionBankCategoryMapper.selectById(id);
    }

    @Override
    public QuestionBankCategory getByName(String categoryName) {
        if (!StringUtils.hasText(categoryName)) {
            return null;
        }
        return questionBankCategoryMapper.selectOne(new LambdaQueryWrapper<QuestionBankCategory>()
                .eq(QuestionBankCategory::getCategoryName, categoryName)
                .last("limit 1"));
    }

    @Override
    public List<QuestionBankCategory> listQuestionBankCategory(ListQuestionBankCategoryQuery query) {
        LambdaQueryWrapper<QuestionBankCategory> wrapper = new LambdaQueryWrapper<QuestionBankCategory>()
                .orderByAsc(QuestionBankCategory::getSortOrder)
                .orderByDesc(QuestionBankCategory::getId);
        if (query != null) {
            if (StringUtils.hasText(query.getCategoryName())) {
                wrapper.like(QuestionBankCategory::getCategoryName, query.getCategoryName());
            }
            if (query.getStatus() != null) {
                wrapper.eq(QuestionBankCategory::getStatus, query.getStatus());
            }
        }
        return questionBankCategoryMapper.selectList(wrapper);
    }

    @Override
    public List<QuestionBankCategory> listByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return questionBankCategoryMapper.selectBatchIds(ids);
    }

    @Override
    public QuestionBankCategory save(QuestionBankCategory category) {
        if (category.getId() == null) {
            questionBankCategoryMapper.insert(category);
            return category;
        }
        questionBankCategoryMapper.updateById(category);
        return questionBankCategoryMapper.selectById(category.getId());
    }

    @Override
    public void deleteById(Long id) {
        questionBankCategoryMapper.deleteById(id);
    }
}
