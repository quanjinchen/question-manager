package cn.spring.arch.system.entity;

import cn.spring.arch.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class Question extends BaseEntity {

    private Long categoryId;

    private String questionType;

    private String title;

    private String optionsJson;

    private String answer;

    private String analysis;

    private BigDecimal score;

    private Integer sortOrder;

    private Integer status;
}

