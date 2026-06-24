package cn.spring.arch.console.pojo.req.question;

import cn.spring.arch.common.page.PageReqParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ListQuestionCategoryReqParam extends PageReqParam {

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "状态")
    private Integer status;
}

