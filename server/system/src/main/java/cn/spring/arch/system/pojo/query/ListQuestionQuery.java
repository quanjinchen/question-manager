package cn.spring.arch.system.pojo.query;

import lombok.Data;

@Data
public class ListQuestionQuery {

    private Long categoryId;

    private String questionType;

    private String title;

    private Integer status;
}

