package cn.spring.arch.consumer.pojo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class QuestionDTO {

    @Schema(description = "题目 ID")
    private Long id;

    @Schema(description = "分类 ID")
    private Long categoryId;

    @Schema(description = "题型")
    private String questionType;

    @Schema(description = "题干")
    private String title;

    @Schema(description = "选项 JSON")
    private String optionsJson;

    @Schema(description = "分值")
    private BigDecimal score;

    @Schema(description = "排序值")
    private Integer sortOrder;

    @Schema(description = "状态")
    private Integer status;
}

