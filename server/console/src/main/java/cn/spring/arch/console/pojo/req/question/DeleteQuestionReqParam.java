package cn.spring.arch.console.pojo.req.question;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DeleteQuestionReqParam {

    @Schema(description = "题目 ID")
    @NotNull(message = "题目 ID 不能为空")
    private Long questionId;
}

