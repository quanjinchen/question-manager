package cn.spring.arch.auth.pojo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "认证结果")
public class AuthResultDTO {

    @Schema(description = "认证令牌")
    private String certToken;

    @Schema(description = "是否已认证")
    private Boolean authenticated;

    @Schema(description = "认证通过用户信息")
    private CertUserDTO userInfo;
}
