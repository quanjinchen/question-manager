package cn.spring.arch.console.pojo.req.question;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateQuestionCategoryReqParam {

    @Schema(description = "分类名称")
    @NotBlank(message = "分类名称不能为空")
    private String categoryName;

    @Schema(description = "分类描述")
    private String description;

    @Schema(description = "排序值")
    private Integer sortOrder;

    @Schema(description = "状态，1 启用，0 禁用")
    private Integer status;
}

