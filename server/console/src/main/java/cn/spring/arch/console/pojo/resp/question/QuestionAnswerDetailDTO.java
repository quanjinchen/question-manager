package cn.spring.arch.console.pojo.resp.question;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class QuestionAnswerDetailDTO {

    @Schema(description = "题目 ID")
    private Long questionId;

    @Schema(description = "题型")
    private String questionType;

    @Schema(description = "题干")
    private String title;

    @Schema(description = "选项 JSON")
    private String optionsJson;

    @Schema(description = "用户答案")
    private String userAnswer;

    @Schema(description = "正确答案")
    private String correctAnswer;

    @Schema(description = "是否正确")
    private Boolean correctFlag;

    @Schema(description = "分值")
    private BigDecimal score;

    @Schema(description = "用户得分")
    private BigDecimal userScore;

    @Schema(description = "解析")
    private String analysis;
}

