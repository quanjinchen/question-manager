package cn.spring.arch.console.pojo.req.question;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class CreateQuestionReqParam {

    @Schema(description = "分类 ID")
    @NotNull(message = "分类 ID 不能为空")
    private Long categoryId;

    @Schema(description = "题型，SINGLE 单选，MULTIPLE 多选，JUDGE 判断，QA 问答")
    @NotBlank(message = "题型不能为空")
    private String questionType;

    @Schema(description = "题干")
    @NotBlank(message = "题干不能为空")
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

    @Schema(description = "状态，1 启用，0 禁用")
    private Integer status;
}
