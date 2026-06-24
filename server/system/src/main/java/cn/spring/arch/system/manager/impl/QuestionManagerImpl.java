package cn.spring.arch.system.manager.impl;

import cn.spring.arch.system.entity.Question;
import cn.spring.arch.system.manager.QuestionManager;
import cn.spring.arch.system.mapper.QuestionMapper;
import cn.spring.arch.system.pojo.query.ListQuestionQuery;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
public class QuestionManagerImpl implements QuestionManager {

    @Resource
    private QuestionMapper questionMapper;

    @Override
    public Question getById(Long id) {
        return questionMapper.selectById(id);
    }

    @Override
    public List<Question> listQuestion(ListQuestionQuery query) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<Question>()
                .orderByAsc(Question::getSortOrder)
                .orderByAsc(Question::getId);
        if (query != null) {
            if (query.getCategoryId() != null) {
                wrapper.eq(Question::getCategoryId, query.getCategoryId());
            }
            if (StringUtils.hasText(query.getQuestionType())) {
                wrapper.eq(Question::getQuestionType, query.getQuestionType());
            }
            if (StringUtils.hasText(query.getTitle())) {
                wrapper.like(Question::getTitle, query.getTitle());
            }
            if (query.getStatus() != null) {
                wrapper.eq(Question::getStatus, query.getStatus());
            }
        }
        return questionMapper.selectList(wrapper);
    }

    @Override
    public long countByCategoryId(Long categoryId) {
        return questionMapper.selectCount(new LambdaQueryWrapper<Question>()
                .eq(Question::getCategoryId, categoryId));
    }

    @Override
    public Question save(Question question) {
        if (question.getId() == null) {
            questionMapper.insert(question);
            return question;
        }
        questionMapper.updateById(question);
        return questionMapper.selectById(question.getId());
    }

    @Override
    public void deleteById(Long id) {
        questionMapper.deleteById(id);
    }
}

