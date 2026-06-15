package cn.spring.arch.auth.pojo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "账户登录信息")
public class AccountLoginDTO {

    @Schema(description = "登录 token")
    private String token;

    @Schema(description = "应用信息")
    private AppInfoDTO appInfo;

    @Schema(description = "用户信息")
    private CertUserDTO userInfo;

}
