package org.lan.iti.common.pay.enums;

import cn.hutool.core.util.StrUtil;
import org.lan.iti.common.core.exception.BusinessException;
import org.lan.iti.common.pay.reason.PaymentExceptionReason;

import java.util.Locale;

/**
 * @author I'm
 * @date 2021/9/14
 */
public enum MethodTypeEnum {

    REQUEST("REQUEST"),
    NOTIFY("NOTIFY"),
    ;

    private final String type;

    MethodTypeEnum(String type) {
        this.type = type;
    }

    public static String getType(String type) {
        if (StrUtil.isBlank(type)) {
            throw BusinessException.withReason(PaymentExceptionReason.Payment.METHOD_TYPE_NOT_FOUND);
        }
        return valueOf(type.toUpperCase(Locale.ROOT)).toString();
    }

    @Override
    public String toString() {
        return this.type;
    }

}
