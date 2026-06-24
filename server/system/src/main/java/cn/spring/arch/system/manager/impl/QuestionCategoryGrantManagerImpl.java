package cn.spring.arch.system.manager.impl;

import cn.spring.arch.system.entity.QuestionCategoryGrant;
import cn.spring.arch.system.manager.QuestionCategoryGrantManager;
import cn.spring.arch.system.mapper.QuestionCategoryGrantMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class QuestionCategoryGrantManagerImpl implements QuestionCategoryGrantManager {

    @Resource
    private QuestionCategoryGrantMapper questionCategoryGrantMapper;

    @Override
    public QuestionCategoryGrant getByUserIdAndCategoryId(Long userId, Long categoryId) {
        return questionCategoryGrantMapper.selectOne(new LambdaQueryWrapper<QuestionCategoryGrant>()
                .eq(QuestionCategoryGrant::getUserId, userId)
                .eq(QuestionCategoryGrant::getCategoryId, categoryId)
                .last("limit 1"));
    }

    @Override
    public List<QuestionCategoryGrant> listByUserId(Long userId) {
        return questionCategoryGrantMapper.selectList(new LambdaQueryWrapper<QuestionCategoryGrant>()
                .eq(QuestionCategoryGrant::getUserId, userId)
                .orderByAsc(QuestionCategoryGrant::getCategoryId));
    }

    @Override
    public List<QuestionCategoryGrant> listByCategoryId(Long categoryId) {
        return questionCategoryGrantMapper.selectList(new LambdaQueryWrapper<QuestionCategoryGrant>()
                .eq(QuestionCategoryGrant::getCategoryId, categoryId)
                .orderByAsc(QuestionCategoryGrant::getUserId));
    }

    @Override
    public List<QuestionCategoryGrant> listByUserIds(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }
        return questionCategoryGrantMapper.selectList(new LambdaQueryWrapper<QuestionCategoryGrant>()
                .in(QuestionCategoryGrant::getUserId, userIds));
    }

    @Override
    public void deleteByUserId(Long userId) {
        questionCategoryGrantMapper.delete(new LambdaQueryWrapper<QuestionCategoryGrant>()
                .eq(QuestionCategoryGrant::getUserId, userId));
    }

    @Override
    public void saveBatch(Long userId, List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }
        List<QuestionCategoryGrant> grants = new ArrayList<QuestionCategoryGrant>(categoryIds.size());
        for (Long categoryId : categoryIds) {
            QuestionCategoryGrant grant = new QuestionCategoryGrant();
            grant.setUserId(userId);
            grant.setCategoryId(categoryId);
            grants.add(grant);
        }
        for (QuestionCategoryGrant grant : grants) {
            questionCategoryGrantMapper.insert(grant);
        }
    }
}

