package cn.spring.arch.console.pojo.req.question;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UpdateQuestionBankCategoryReqParam {

    @Schema(description = "题库分类 ID")
    @NotNull(message = "题库分类 ID 不能为空")
    private Long id;

    @Schema(description = "题库分类名称")
    @NotBlank(message = "题库分类名称不能为空")
    private String categoryName;

    @Schema(description = "题库分类描述")
    private String description;

    @Schema(description = "排序值")
    private Integer sortOrder;

    @Schema(description = "状态，1 启用，0 禁用")
    private Integer status;
}
