package cn.spring.arch.auth.pojo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "查询认证结果请求")
public class GetAuthResultReqParam {

    @Schema(description = "认证令牌", required = true, example = "3f1d6b5a9c")
    @NotBlank(message = "certToken 不能为空")
    private String certToken;
}
