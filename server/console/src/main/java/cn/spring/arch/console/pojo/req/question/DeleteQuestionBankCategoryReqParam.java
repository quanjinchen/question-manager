package cn.spring.arch.console.pojo.req.question;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DeleteQuestionBankCategoryReqParam {

    @Schema(description = "题库分类 ID")
    @NotNull(message = "题库分类 ID 不能为空")
    private Long categoryId;
}
