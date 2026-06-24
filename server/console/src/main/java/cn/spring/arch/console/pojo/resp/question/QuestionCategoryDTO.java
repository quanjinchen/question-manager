package cn.spring.arch.console.pojo.resp.question;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuestionCategoryDTO {

    @Schema(description = "题库 ID")
    private Long id;

    @Schema(description = "题库分类 ID")
    private Long bankCategoryId;

    @Schema(description = "题库分类名称")
    private String bankCategoryName;

    @Schema(description = "题库名称")
    private String categoryName;

    @Schema(description = "题库描述")
    private String description;

    @Schema(description = "排序值")
    private Integer sortOrder;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "题目数量")
    private Long questionCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
