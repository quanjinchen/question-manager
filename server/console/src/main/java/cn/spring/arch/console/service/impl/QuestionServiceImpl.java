package cn.spring.arch.console.service.impl;

import cn.spring.arch.common.constant.ResultCode;
import cn.spring.arch.common.page.PageData;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.question.CreateQuestionReqParam;
import cn.spring.arch.console.pojo.req.question.DeleteQuestionReqParam;
import cn.spring.arch.console.pojo.req.question.ListQuestionReqParam;
import cn.spring.arch.console.pojo.req.question.UpdateQuestionReqParam;
import cn.spring.arch.console.pojo.resp.question.QuestionDTO;
import cn.spring.arch.console.service.QuestionService;
import cn.spring.arch.system.entity.Question;
import cn.spring.arch.system.entity.QuestionCategory;
import cn.spring.arch.system.manager.QuestionCategoryManager;
import cn.spring.arch.system.manager.QuestionManager;
import cn.spring.arch.system.pojo.query.ListQuestionQuery;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Resource
    private QuestionManager questionManager;
    @Resource
    private QuestionCategoryManager questionCategoryManager;

    @Override
    public RespInfo<Void> createQuestion(CreateQuestionReqParam reqParam) {
        QuestionCategory category = questionCategoryManager.getById(reqParam.getCategoryId());
        ResultCode.BAD_REQUEST.assertNotNull(category, "题目分类不存在");
        ResultCode.BAD_REQUEST.assertIsTrue("QA".equals(reqParam.getQuestionType()) || StringUtils.hasText(reqParam.getAnswer()), "客观题正确答案不能为空");

        Question question = new Question();
        fillQuestion(question, reqParam.getCategoryId(), reqParam.getQuestionType(), reqParam.getTitle(), reqParam.getOptionsJson(), reqParam.getAnswer(), reqParam.getAnalysis(), reqParam.getScore(), reqParam.getSortOrder(), reqParam.getStatus());
        questionManager.save(question);
        return RespInfo.success();
    }

    @Override
    public RespInfo<Void> updateQuestion(UpdateQuestionReqParam reqParam) {
        Question question = questionManager.getById(reqParam.getId());
        ResultCode.BAD_REQUEST.assertNotNull(question, "题目不存在");
        QuestionCategory category = questionCategoryManager.getById(reqParam.getCategoryId());
        ResultCode.BAD_REQUEST.assertNotNull(category, "题目分类不存在");
        ResultCode.BAD_REQUEST.assertIsTrue("QA".equals(reqParam.getQuestionType()) || StringUtils.hasText(reqParam.getAnswer()), "客观题正确答案不能为空");

        fillQuestion(question, reqParam.getCategoryId(), reqParam.getQuestionType(), reqParam.getTitle(), reqParam.getOptionsJson(), reqParam.getAnswer(), reqParam.getAnalysis(), reqParam.getScore(), reqParam.getSortOrder(), reqParam.getStatus());
        questionManager.save(question);
        return RespInfo.success();
    }

    @Override
    public RespInfo<Void> deleteQuestion(DeleteQuestionReqParam reqParam) {
        Question question = questionManager.getById(reqParam.getQuestionId());
        ResultCode.BAD_REQUEST.assertNotNull(question, "题目不存在");
        questionManager.deleteById(reqParam.getQuestionId());
        return RespInfo.success();
    }

    @Override
    public RespInfo<QuestionDTO> getQuestionById(Long id) {
        Question question = questionManager.getById(id);
        ResultCode.BAD_REQUEST.assertNotNull(question, "题目不存在");
        return RespInfo.success(convertQuestion(question));
    }

    @Override
    public RespInfo<PageData<QuestionDTO>> listQuestion(ListQuestionReqParam reqParam) {
        PageHelper.startPage(reqParam.getPageNum(), reqParam.getPageSize());
        ListQuestionQuery query = new ListQuestionQuery();
        query.setCategoryId(reqParam.getCategoryId());
        query.setQuestionType(reqParam.getQuestionType());
        query.setTitle(reqParam.getTitle());
        query.setStatus(reqParam.getStatus());
        List<Question> questions = questionManager.listQuestion(query);

        PageData<QuestionDTO> pageData = new PageData<QuestionDTO>();
        pageData.setPageNum(reqParam.getPageNum());
        pageData.setPageSize(reqParam.getPageSize());
        pageData.setTotal(new PageInfo<Question>(questions).getTotal());
        pageData.setRecords(questions.stream().map(this::convertQuestion).collect(Collectors.toList()));
        return RespInfo.success(pageData);
    }

    private void fillQuestion(Question question, Long categoryId, String questionType, String title, String optionsJson, String answer, String analysis, BigDecimal score, Integer sortOrder, Integer status) {
        question.setCategoryId(categoryId);
        question.setQuestionType(questionType);
        question.setTitle(title);
        question.setOptionsJson(optionsJson);
        question.setAnswer(answer);
        question.setAnalysis(analysis);
        question.setScore(score == null ? BigDecimal.ONE : score);
        question.setSortOrder(sortOrder == null ? 0 : sortOrder);
        question.setStatus(status == null ? 1 : status);
    }

    private QuestionDTO convertQuestion(Question question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        dto.setCategoryId(question.getCategoryId());
        QuestionCategory category = questionCategoryManager.getById(question.getCategoryId());
        dto.setCategoryName(category == null ? "" : category.getCategoryName());
        dto.setQuestionType(question.getQuestionType());
        dto.setTitle(question.getTitle());
        dto.setOptionsJson(question.getOptionsJson());
        dto.setAnswer(question.getAnswer());
        dto.setAnalysis(question.getAnalysis());
        dto.setScore(question.getScore());
        dto.setSortOrder(question.getSortOrder());
        dto.setStatus(question.getStatus());
        dto.setCreateTime(question.getCreateTime());
        return dto;
    }
}
