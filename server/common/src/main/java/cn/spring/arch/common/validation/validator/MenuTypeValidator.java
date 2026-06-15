package cn.spring.arch.common.validation.validator;

import cn.hutool.core.util.StrUtil;
import cn.spring.arch.common.annotation.MenuType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MenuTypeValidator implements ConstraintValidator<MenuType, String> {
    private static final Set<String> ALLOWED_TYPES = new HashSet<String>(Arrays.asList("DIR", "MENU", "PAGE", "BTN"));

    private boolean allowBlank;

    @Override
    public void initialize(MenuType constraintAnnotation) {
        this.allowBlank = constraintAnnotation.allowBlank();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StrUtil.isBlank(value)) {
            return allowBlank;
        }
        return ALLOWED_TYPES.contains(value);
    }
}

