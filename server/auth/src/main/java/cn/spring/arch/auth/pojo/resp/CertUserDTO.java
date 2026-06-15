package cn.spring.arch.auth.pojo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "认证通过用户信息")
public class CertUserDTO {

    @Schema(description = "用户 ID")
    private Long userId;

    @Schema(description = "姓名")
    private String fullName;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "手机号")
    private String phone;

}
