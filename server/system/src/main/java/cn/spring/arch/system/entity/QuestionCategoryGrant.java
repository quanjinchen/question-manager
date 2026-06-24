package cn.spring.arch.system.entity;

import cn.spring.arch.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionCategoryGrant extends BaseEntity {

    private Long categoryId;

    private Long userId;
}

