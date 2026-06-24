package cn.spring.arch.console.pojo.resp.question;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuestionBankCategoryDTO {

    @Schema(description = "题库分类 ID")
    private Long id;

    @Schema(description = "题库分类名称")
    private String categoryName;

    @Schema(description = "题库分类描述")
    private String description;

    @Schema(description = "题库数量")
    private Long bankCount;

    @Schema(description = "排序值")
    private Integer sortOrder;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
