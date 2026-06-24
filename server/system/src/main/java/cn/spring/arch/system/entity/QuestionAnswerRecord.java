package cn.spring.arch.system.entity;

import cn.spring.arch.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionAnswerRecord extends BaseEntity {

    private Long userId;

    private Long categoryId;

    private BigDecimal totalScore;

    private BigDecimal userScore;

    private Integer questionCount;

    private Integer correctCount;
}

