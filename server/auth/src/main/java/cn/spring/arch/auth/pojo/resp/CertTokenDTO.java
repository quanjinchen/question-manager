package cn.spring.arch.auth.pojo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "认证令牌信息")
public class CertTokenDTO {

    @Schema(description = "认证令牌")
    private String certToken;

    @Schema(description = "认证页面地址")
    private String authUrl;

    @Schema(description = "应用信息")
    private AppInfoDTO appInfo;
}
