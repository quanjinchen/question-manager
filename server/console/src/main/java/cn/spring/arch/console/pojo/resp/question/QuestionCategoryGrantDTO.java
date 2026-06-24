package cn.spring.arch.console.pojo.resp.question;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class QuestionCategoryGrantDTO {

    @Schema(description = "用户 ID")
    private Long userId;

    @Schema(description = "已授权分类 ID 列表")
    private List<Long> categoryIds;
}

