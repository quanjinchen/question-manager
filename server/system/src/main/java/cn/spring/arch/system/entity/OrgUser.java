package cn.spring.arch.system.entity;

import cn.spring.arch.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrgUser extends BaseEntity {

    @TableField("org_id")
    private Long orgId;

    @TableField("user_id")
    private Long userId;
}

