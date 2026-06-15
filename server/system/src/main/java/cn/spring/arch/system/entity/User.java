package cn.spring.arch.system.entity;

import cn.spring.arch.common.entity.BaseEntity;
import cn.spring.arch.common.entity.EncryptField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    private String username;

    private String fullName;

    private String email;

    private EncryptField phone;

    private EncryptField idCard;

    private String password;

    private Integer status;
}

