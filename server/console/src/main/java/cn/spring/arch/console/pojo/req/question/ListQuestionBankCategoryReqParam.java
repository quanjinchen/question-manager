package cn.spring.arch.console.pojo.req.question;

import cn.spring.arch.common.page.PageReqParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ListQuestionBankCategoryReqParam extends PageReqParam {

    @Schema(description = "题库分类名称")
    private String categoryName;

    @Schema(description = "状态")
    private Integer status;
}
