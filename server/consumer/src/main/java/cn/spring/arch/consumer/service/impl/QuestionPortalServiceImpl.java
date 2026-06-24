package cn.spring.arch.consumer.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.spring.arch.common.constant.ResultCode;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.consumer.pojo.req.QuestionAnswerItemReqParam;
import cn.spring.arch.consumer.pojo.req.SubmitQuestionAnswerReqParam;
import cn.spring.arch.consumer.pojo.req.UserLoginReqParam;
import cn.spring.arch.consumer.pojo.resp.QuestionAnswerDetailDTO;
import cn.spring.arch.consumer.pojo.resp.QuestionAnswerRecordDTO;
import cn.spring.arch.consumer.pojo.resp.QuestionCategoryDTO;
import cn.spring.arch.consumer.pojo.resp.QuestionDTO;
import cn.spring.arch.consumer.pojo.resp.UserLoginDTO;
import cn.spring.arch.consumer.service.QuestionPortalService;
import cn.spring.arch.system.entity.Question;
import cn.spring.arch.system.entity.QuestionAnswerDetail;
import cn.spring.arch.system.entity.QuestionAnswerRecord;
import cn.spring.arch.system.entity.QuestionCategory;
import cn.spring.arch.system.entity.QuestionCategoryGrant;
import cn.spring.arch.system.entity.User;
import cn.spring.arch.system.manager.QuestionAnswerDetailManager;
import cn.spring.arch.system.manager.QuestionAnswerRecordManager;
import cn.spring.arch.system.manager.QuestionCategoryGrantManager;
import cn.spring.arch.system.manager.QuestionCategoryManager;
import cn.spring.arch.system.manager.QuestionManager;
import cn.spring.arch.system.manager.UserManager;
import cn.spring.arch.system.pojo.query.ListQuestionQuery;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class QuestionPortalServiceImpl implements QuestionPortalService {

    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Resource
    private UserManager userManager;
    @Resource
    private QuestionCategoryGrantManager questionCategoryGrantManager;
    @Resource
    private QuestionCategoryManager questionCategoryManager;
    @Resource
    private QuestionManager questionManager;
    @Resource
    private QuestionAnswerRecordManager questionAnswerRecordManager;
    @Resource
    private QuestionAnswerDetailManager questionAnswerDetailManager;

    @Override
    public RespInfo<UserLoginDTO> login(UserLoginReqParam reqParam) {
        User user = userManager.getByAccount(reqParam.getAccount());
        ResultCode.ACCOUNT_OR_PASSWORD_INVALID.assertNotNull(user);
        if (user.getStatus() != null && user.getStatus() != 1) {
            throw ResultCode.FORBIDDEN.newException("账号已禁用");
        }
        ResultCode.ACCOUNT_OR_PASSWORD_INVALID.assertIsTrue(PASSWORD_ENCODER.matches(reqParam.getPassword(), user.getPassword()));

        StpUtil.login(user.getId());
        UserLoginDTO dto = new UserLoginDTO();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setToken(StpUtil.getTokenValue());
        return RespInfo.success(dto);
    }

    @Override
    public RespInfo<Void> logout() {
        StpUtil.logout();
        return RespInfo.success();
    }

    @Override
    public RespInfo<List<QuestionCategoryDTO>> listMyQuestionCategory() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<Long> categoryIds = questionCategoryGrantManager.listByUserId(userId).stream()
                .map(QuestionCategoryGrant::getCategoryId)
                .collect(Collectors.toList());
        List<QuestionCategoryDTO> list = questionCategoryManager.listByIds(categoryIds).stream()
                .filter(category -> category.getStatus() != null && category.getStatus() == 1)
                .map(this::convertCategory)
                .collect(Collectors.toList());
        return RespInfo.success(list);
    }

    @Override
    public RespInfo<List<QuestionDTO>> listQuestionByCategoryId(Long categoryId) {
        assertUserCanAccessCategory(categoryId);
        ListQuestionQuery query = new ListQuestionQuery();
        query.setCategoryId(categoryId);
        query.setStatus(1);
        return RespInfo.success(questionManager.listQuestion(query).stream()
                .map(this::convertQuestionWithoutAnswer)
                .collect(Collectors.toList()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RespInfo<QuestionAnswerRecordDTO> submitQuestionAnswer(SubmitQuestionAnswerReqParam reqParam) {
        Long userId = StpUtil.getLoginIdAsLong();
        assertUserCanAccessCategory(reqParam.getCategoryId());

        ListQuestionQuery query = new ListQuestionQuery();
        query.setCategoryId(reqParam.getCategoryId());
        query.setStatus(1);
        List<Question> questions = questionManager.listQuestion(query);
        Map<Long, Question> questionMap = questions.stream().collect(Collectors.toMap(Question::getId, Function.identity()));

        BigDecimal totalScore = BigDecimal.ZERO;
        BigDecimal userScore = BigDecimal.ZERO;
        int correctCount = 0;
        List<QuestionAnswerDetail> details = new ArrayList<QuestionAnswerDetail>();
        for (QuestionAnswerItemReqParam answerItem : reqParam.getAnswers()) {
            Question question = questionMap.get(answerItem.getQuestionId());
            ResultCode.BAD_REQUEST.assertNotNull(question, "存在无效题目");
            BigDecimal questionScore = question.getScore() == null ? BigDecimal.ONE : question.getScore();
            totalScore = totalScore.add(questionScore);

            boolean correct = !"QA".equals(question.getQuestionType()) && isAnswerMatched(question.getAnswer(), answerItem.getUserAnswer());
            BigDecimal itemUserScore = correct ? questionScore : BigDecimal.ZERO;
            if (correct) {
                correctCount++;
            }
            userScore = userScore.add(itemUserScore);

            QuestionAnswerDetail detail = new QuestionAnswerDetail();
            detail.setQuestionId(question.getId());
            detail.setUserAnswer(answerItem.getUserAnswer());
            detail.setCorrectAnswer(question.getAnswer());
            detail.setCorrectFlag(correct);
            detail.setScore(questionScore);
            detail.setUserScore(itemUserScore);
            details.add(detail);
        }

        QuestionAnswerRecord record = new QuestionAnswerRecord();
        record.setUserId(userId);
        record.setCategoryId(reqParam.getCategoryId());
        record.setTotalScore(totalScore);
        record.setUserScore(userScore);
        record.setQuestionCount(details.size());
        record.setCorrectCount(correctCount);
        questionAnswerRecordManager.save(record);

        for (QuestionAnswerDetail detail : details) {
            detail.setRecordId(record.getId());
        }
        questionAnswerDetailManager.saveBatch(details);
        return RespInfo.success(convertRecord(record, true));
    }

    @Override
    public RespInfo<QuestionAnswerRecordDTO> getQuestionAnswerRecordById(Long recordId) {
        QuestionAnswerRecord record = questionAnswerRecordManager.getById(recordId);
        ResultCode.BAD_REQUEST.assertNotNull(record, "答题记录不存在");
        ResultCode.FORBIDDEN.assertIsTrue(record.getUserId().equals(StpUtil.getLoginIdAsLong()), "无权查看该答题记录");
        return RespInfo.success(convertRecord(record, true));
    }

    private void assertUserCanAccessCategory(Long categoryId) {
        QuestionCategory category = questionCategoryManager.getById(categoryId);
        ResultCode.BAD_REQUEST.assertNotNull(category, "题目分类不存在");
        if (category.getStatus() == null || category.getStatus() != 1) {
            throw ResultCode.FORBIDDEN.newException("题目分类已禁用");
        }
        QuestionCategoryGrant grant = questionCategoryGrantManager.getByUserIdAndCategoryId(StpUtil.getLoginIdAsLong(), categoryId);
        ResultCode.FORBIDDEN.assertNotNull(grant, "未授权访问该题目分类");
    }

    private boolean isAnswerMatched(String correctAnswer, String userAnswer) {
        if (!StringUtils.hasText(correctAnswer) || !StringUtils.hasText(userAnswer)) {
            return false;
        }
        Set<String> correctSet = normalizeAnswer(correctAnswer);
        Set<String> userSet = normalizeAnswer(userAnswer);
        return correctSet.equals(userSet);
    }

    private Set<String> normalizeAnswer(String answer) {
        return Arrays.stream(answer.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private QuestionCategoryDTO convertCategory(QuestionCategory category) {
        QuestionCategoryDTO dto = new QuestionCategoryDTO();
        dto.setId(category.getId());
        dto.setCategoryName(category.getCategoryName());
        dto.setDescription(category.getDescription());
        dto.setSortOrder(category.getSortOrder());
        dto.setStatus(category.getStatus());
        dto.setQuestionCount(questionManager.countByCategoryId(category.getId()));
        return dto;
    }

    private QuestionDTO convertQuestionWithoutAnswer(Question question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        dto.setCategoryId(question.getCategoryId());
        dto.setQuestionType(question.getQuestionType());
        dto.setTitle(question.getTitle());
        dto.setOptionsJson(question.getOptionsJson());
        dto.setScore(question.getScore());
        dto.setSortOrder(question.getSortOrder());
        dto.setStatus(question.getStatus());
        return dto;
    }

    private QuestionAnswerRecordDTO convertRecord(QuestionAnswerRecord record, boolean withDetail) {
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
