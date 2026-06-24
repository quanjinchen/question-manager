package cn.spring.arch.consumer.pojo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QuestionAnswerItemReqParam {

    @Schema(description = "题目 ID")
    @NotNull(message = "题目 ID 不能为空")
    private Long questionId;

    @Schema(description = "用户答案")
    private String userAnswer;
}

