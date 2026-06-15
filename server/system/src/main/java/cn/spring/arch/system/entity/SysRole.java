package cn.spring.arch.system.entity;

import cn.spring.arch.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysRole extends BaseEntity {

    private String code;

    private String name;

    private Integer status;

    private String remark;
}

