package cn.spring.arch.system.manager.impl;

import cn.spring.arch.system.entity.QuestionAnswerDetail;
import cn.spring.arch.system.manager.QuestionAnswerDetailManager;
import cn.spring.arch.system.mapper.QuestionAnswerDetailMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class QuestionAnswerDetailManagerImpl implements QuestionAnswerDetailManager {

    @Resource
    private QuestionAnswerDetailMapper questionAnswerDetailMapper;

    @Override
    public List<QuestionAnswerDetail> listByRecordId(Long recordId) {
        return questionAnswerDetailMapper.selectList(new LambdaQueryWrapper<QuestionAnswerDetail>()
                .eq(QuestionAnswerDetail::getRecordId, recordId)
                .orderByAsc(QuestionAnswerDetail::getId));
    }

    @Override
    public void saveBatch(List<QuestionAnswerDetail> details) {
        if (details == null || details.isEmpty()) {
            return;
        }
        for (QuestionAnswerDetail detail : details) {
            questionAnswerDetailMapper.insert(detail);
        }
    }
}

