package cn.spring.arch.console.pojo.resp.question;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class QuestionDTO {

    @Schema(description = "题目 ID")
    private Long id;

    @Schema(description = "分类 ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "题型")
    private String questionType;

    @Schema(description = "题干")
    private String title;

    @Schema(description = "选项 JSON")
    private String optionsJson;

    @Schema(description = "正确答案")
    private String answer;

    @Schema(description = "答案解析")
    private String analysis;

    @Schema(description = "分值")
    private BigDecimal score;

    @Schema(description = "排序值")
    private Integer sortOrder;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

