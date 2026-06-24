package cn.spring.arch.system.pojo.query;

import lombok.Data;

@Data
public class ListQuestionCategoryQuery {

    private Long bankCategoryId;

    private String categoryName;

    private Integer status;
}

