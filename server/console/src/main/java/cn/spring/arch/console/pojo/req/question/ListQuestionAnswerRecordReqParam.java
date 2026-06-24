package cn.spring.arch.console.pojo.req.question;

import cn.spring.arch.common.page.PageReqParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ListQuestionAnswerRecordReqParam extends PageReqParam {

    @Schema(description = "用户 ID")
    private Long userId;

    @Schema(description = "分类 ID")
    private Long categoryId;
}

