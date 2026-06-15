package cn.spring.arch.system.enums;

import cn.spring.arch.common.entity.CodeEnum;

public enum FaceAuthApiTypeEnum implements CodeEnum<Integer> {

    ONE_TO_ONE(1),

    ONE_TO_N(2);

    private final Integer code;

    FaceAuthApiTypeEnum(Integer code) {
        this.code = code;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}

