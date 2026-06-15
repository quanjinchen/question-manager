package cn.spring.arch.auth.pojo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "应用信息")
public class AppInfoDTO {

    @Schema(description = "应用名称")
    private String appName;

    @Schema(description = "应用账号")
    private String clientId;
}
