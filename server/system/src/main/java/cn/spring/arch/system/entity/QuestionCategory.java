package cn.spring.arch.system.entity;

import cn.spring.arch.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionCategory extends BaseEntity {

    private Long bankCategoryId;

    private String categoryName;

    private String description;

    private Integer sortOrder;

    private Integer status;
}

