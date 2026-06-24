package cn.spring.arch.system.entity;

import cn.spring.arch.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionAnswerDetail extends BaseEntity {

    private Long recordId;

    private Long questionId;

    private String userAnswer;

    private String correctAnswer;

    private Boolean correctFlag;

    private BigDecimal score;

    private BigDecimal userScore;
}

