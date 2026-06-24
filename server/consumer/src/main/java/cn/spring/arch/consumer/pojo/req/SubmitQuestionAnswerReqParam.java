package cn.spring.arch.consumer.pojo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class SubmitQuestionAnswerReqParam {

    @Schema(description = "分类 ID")
    @NotNull(message = "分类 ID 不能为空")
    private Long categoryId;

    @Schema(description = "用户答案列表")
    @Valid
    @NotEmpty(message = "用户答案不能为空")
    private List<QuestionAnswerItemReqParam> answers;
}

