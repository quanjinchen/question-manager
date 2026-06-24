package cn.spring.arch.console.pojo.req.question;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class GrantQuestionCategoryReqParam {

    @Schema(description = "用户 ID")
    @NotNull(message = "用户 ID 不能为空")
    private Long userId;

    @Schema(description = "分类 ID 列表")
    private List<Long> categoryIds;
}

