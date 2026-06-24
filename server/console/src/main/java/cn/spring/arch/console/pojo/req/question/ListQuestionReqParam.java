package cn.spring.arch.console.pojo.req.question;

import cn.spring.arch.common.page.PageReqParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ListQuestionReqParam extends PageReqParam {

    @Schema(description = "分类 ID")
    private Long categoryId;

    @Schema(description = "题型")
    private String questionType;

    @Schema(description = "题干")
    private String title;

    @Schema(description = "状态")
    private Integer status;
}

