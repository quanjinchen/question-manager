package cn.spring.arch.consumer.pojo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class QuestionCategoryDTO {

    @Schema(description = "分类 ID")
    private Long id;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "分类描述")
    private String description;

    @Schema(description = "排序值")
    private Integer sortOrder;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "题目数量")
    private Long questionCount;
}

