package org.lan.iti.common.pay.enums;

import lombok.Getter;

/**
 * @author I'm
 * @since 2021/3/22
 * description 错误类型枚举
 */
@Getter
public enum ErrorLevelEnum {

    // 致命级，可能影响整个系统
    FATAL(1),
    // 严重错误，可能影响整个服务
    IMPORTANT(2),
    // 主要错误，可能影响整个业务
    PRIMARY(3),
    // 普通错误，对业务流程上不造成重大影响
    NORMAL(4),
    // 可忽略错误，仅仅作为错误提示，没有任何影响
    IGNORE(5);

    ErrorLevelEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    private final int value;
}