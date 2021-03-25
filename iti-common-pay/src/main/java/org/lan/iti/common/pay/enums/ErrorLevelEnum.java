package org.lan.iti.common.pay.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author I'm
 * @since 2021/3/22
 * description 错误类型枚举
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ErrorLevelEnum {

    FATAL(1), // 致命级，可能影响整个系统
    IMPORTANT(2), // 严重错误，可能影响整个服务
    PRIMARY(3), // 主要错误，可能影响整个业务
    NORMAL(4), // 普通错误，对业务流程上不造成重大影响
    IGNORE(5) // 可忽略错误，仅仅作为错误提示，没有任何影响
    ;
    private final int value;
}