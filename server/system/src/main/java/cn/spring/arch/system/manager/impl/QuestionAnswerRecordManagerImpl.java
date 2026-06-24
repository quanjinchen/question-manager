package cn.spring.arch.system.manager.impl;

import cn.spring.arch.system.entity.QuestionAnswerRecord;
import cn.spring.arch.system.manager.QuestionAnswerRecordManager;
import cn.spring.arch.system.mapper.QuestionAnswerRecordMapper;
import cn.spring.arch.system.pojo.query.ListQuestionAnswerRecordQuery;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class QuestionAnswerRecordManagerImpl implements QuestionAnswerRecordManager {

    @Resource
    private QuestionAnswerRecordMapper questionAnswerRecordMapper;

    @Override
    public QuestionAnswerRecord getById(Long id) {
        return questionAnswerRecordMapper.selectById(id);
    }

    @Override
    public List<QuestionAnswerRecord> listQuestionAnswerRecord(ListQuestionAnswerRecordQuery query) {
        LambdaQueryWrapper<QuestionAnswerRecord> wrapper = new LambdaQueryWrapper<QuestionAnswerRecord>()
                .orderByDesc(QuestionAnswerRecord::getId);
        if (query != null) {
            if (query.getUserId() != null) {
                wrapper.eq(QuestionAnswerRecord::getUserId, query.getUserId());
            }
            if (query.getCategoryId() != null) {
                wrapper.eq(QuestionAnswerRecord::getCategoryId, query.getCategoryId());
            }
        }
        return questionAnswerRecordMapper.selectList(wrapper);
    }

    @Override
    public QuestionAnswerRecord save(QuestionAnswerRecord record) {
        if (record.getId() == null) {
            questionAnswerRecordMapper.insert(record);
            return record;
        }
        questionAnswerRecordMapper.updateById(record);
        return questionAnswerRecordMapper.selectById(record.getId());
    }
}

