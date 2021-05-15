package org.lan.iti.common.pay.enums;

/**
 * @author I'm
 * @since 2021/3/22
 * description 错误类型枚举
 */
public enum ErrorTypeEnum {

    // 框架内部错误
    FRAMEWORK(1),
    // 框架扩展错误
    EXT(2),
    // 业务错误
    BIZ(3),
    // 第三方错误
    THIRD_PARTY(
            4
    );

    ErrorTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    private final int value;
}