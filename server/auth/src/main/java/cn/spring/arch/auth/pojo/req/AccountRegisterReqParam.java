package cn.spring.arch.auth.pojo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "账户注册请求")
public class AccountRegisterReqParam {

    @Schema(description = "身份证号", required = true, example = "110101199001011234")
    @NotBlank(message = "身份证号不能为空")
    private String idCard;

    @Schema(description = "姓名", required = true, example = "张三")
    @NotBlank(message = "姓名不能为空")
    private String fullName;

    @Schema(description = "认证令牌", required = true, example = "3f1d6b5a9c")
    @NotBlank(message = "certToken 不能为空")
    private String certToken;
}
