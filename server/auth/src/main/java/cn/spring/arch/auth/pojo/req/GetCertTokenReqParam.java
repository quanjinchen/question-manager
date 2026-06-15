package cn.spring.arch.auth.pojo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "获取 certToken 请求")
public class GetCertTokenReqParam {

    @Schema(description = "应用账号", required = true, example = "app-123456789abc")
    @NotBlank(message = "clientId 不能为空")
    private String clientId;

    @Schema(description = "应用秘钥", required = true, example = "1234567890abcdef12345678")
    @NotBlank(message = "clientSecret 不能为空")
    private String clientSecret;
}
