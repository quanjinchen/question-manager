package cn.spring.arch.console.service.impl;

import cn.spring.arch.common.constant.ResultCode;
import cn.spring.arch.common.page.PageData;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.question.ListQuestionAnswerRecordReqParam;
import cn.spring.arch.console.pojo.resp.question.QuestionAnswerDetailDTO;
import cn.spring.arch.console.pojo.resp.question.QuestionAnswerRecordDTO;
import cn.spring.arch.console.service.QuestionAnswerRecordService;
import cn.spring.arch.system.entity.Question;
import cn.spring.arch.system.entity.QuestionAnswerDetail;
import cn.spring.arch.system.entity.QuestionAnswerRecord;
import cn.spring.arch.system.entity.QuestionCategory;
import cn.spring.arch.system.entity.User;
import cn.spring.arch.system.manager.QuestionAnswerDetailManager;
import cn.spring.arch.system.manager.QuestionAnswerRecordManager;
import cn.spring.arch.system.manager.QuestionCategoryManager;
import cn.spring.arch.system.manager.QuestionManager;
import cn.spring.arch.system.manager.UserManager;
import cn.spring.arch.system.pojo.query.ListQuestionAnswerRecordQuery;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionAnswerRecordServiceImpl implements QuestionAnswerRecordService {

    @Resource
    private QuestionAnswerRecordManager questionAnswerRecordManager;
    @Resource
    private QuestionAnswerDetailManager questionAnswerDetailManager;
    @Resource
    private QuestionManager questionManager;
    @Resource
    private QuestionCategoryManager questionCategoryManager;
    @Resource
    private UserManager userManager;

    @Override
    public RespInfo<PageData<QuestionAnswerRecordDTO>> listQuestionAnswerRecord(ListQuestionAnswerRecordReqParam reqParam) {
        PageHelper.startPage(reqParam.getPageNum(), reqParam.getPageSize());
        ListQuestionAnswerRecordQuery query = new ListQuestionAnswerRecordQuery();
        query.setUserId(reqParam.getUserId());
        query.setCategoryId(reqParam.getCategoryId());
        List<QuestionAnswerRecord> records = questionAnswerRecordManager.listQuestionAnswerRecord(query);

        PageData<QuestionAnswerRecordDTO> pageData = new PageData<QuestionAnswerRecordDTO>();
        pageData.setPageNum(reqParam.getPageNum());
        pageData.setPageSize(reqParam.getPageSize());
        pageData.setTotal(new PageInfo<QuestionAnswerRecord>(records).getTotal());
        pageData.setRecords(records.stream().map(record -> convertRecord(record, false)).collect(Collectors.toList()));
        return RespInfo.success(pageData);
    }

    @Override
    public RespInfo<QuestionAnswerRecordDTO> getQuestionAnswerRecordById(Long id) {
        QuestionAnswerRecord record = questionAnswerRecordManager.getById(id);
        ResultCode.BAD_REQUEST.assertNotNull(record, "答题记录不存在");
        return RespInfo.success(convertRecord(record, true));
    }

    public QuestionAnswerRecordDTO convertRecord(QuestionAnswerRecord record, boolean withDetail) {
        QuestionAnswerRecordDTO dto = new QuestionAnswerRecordDTO();
        dto.setId(record.getId());
        dto.setUserId(record.getUserId());
        dto.setCategoryId(record.getCategoryId());
        dto.setTotalScore(record.getTotalScore());
        dto.setUserScore(record.getUserScore());
        dto.setQuestionCount(record.getQuestionCount());
        dto.setCorrectCount(record.getCorrectCount());
        dto.setCreateTime(record.getCreateTime());

        User user = userManager.getById(record.getUserId());
        dto.setUsername(user == null ? "" : user.getUsername());
        dto.setFullName(user == null ? "" : user.getFullName());
        QuestionCategory category = questionCategoryManager.getById(record.getCategoryId());
        dto.setCategoryName(category == null ? "" : category.getCategoryName());

        if (withDetail) {
            dto.setDetails(questionAnswerDetailManager.listByRecordId(record.getId()).stream()
                    .map(this::convertDetail)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private QuestionAnswerDetailDTO convertDetail(QuestionAnswerDetail detail) {
        QuestionAnswerDetailDTO dto = new QuestionAnswerDetailDTO();
        dto.setQuestionId(detail.getQuestionId());
        dto.setUserAnswer(detail.getUserAnswer());
        dto.setCorrectAnswer(detail.getCorrectAnswer());
        dto.setCorrectFlag(detail.getCorrectFlag());
        dto.setScore(detail.getScore());
        dto.setUserScore(detail.getUserScore());

        Question question = questionManager.getById(detail.getQuestionId());
        if (question != null) {
            dto.setQuestionType(question.getQuestionType());
            dto.setTitle(question.getTitle());
            dto.setOptionsJson(question.getOptionsJson());
            dto.setAnalysis(question.getAnalysis());
        }
        return dto;
    }
}

