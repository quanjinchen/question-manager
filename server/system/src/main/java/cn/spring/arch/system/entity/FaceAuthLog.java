package cn.spring.arch.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import cn.spring.arch.common.entity.BaseEntity;
import cn.spring.arch.system.enums.FaceAuthApiTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FaceAuthLog extends BaseEntity {

    @TableField("auth_api_type")
    private FaceAuthApiTypeEnum authApiType;

    private String ip;

    @TableField("app_id")
    private Long appId;

    private String appName;

    private String authFullName;

    @TableField("auth_user_id")
    private Long authUserId;

    private Integer status;

    private String errmsg;
}

