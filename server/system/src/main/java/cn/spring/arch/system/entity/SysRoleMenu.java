package cn.spring.arch.system.entity;

import cn.spring.arch.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysRoleMenu extends BaseEntity {

    @TableField("role_id")
    private Long roleId;

    @TableField("menu_id")
    private Long menuId;
}

