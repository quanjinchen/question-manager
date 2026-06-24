package cn.spring.arch.consumer.pojo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionAnswerRecordDTO {

    @Schema(description = "答题记录 ID")
    private Long id;

    @Schema(description = "用户 ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "姓名")
    private String fullName;

    @Schema(description = "分类 ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "总分")
    private BigDecimal totalScore;

    @Schema(description = "得分")
    private BigDecimal userScore;

    @Schema(description = "题目数")
    private Integer questionCount;

    @Schema(description = "正确题数")
    private Integer correctCount;

    @Schema(description = "提交时间")
    private LocalDateTime createTime;

    @Schema(description = "明细")
    private List<QuestionAnswerDetailDTO> details;
}

