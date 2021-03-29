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
public enum ErrorTypeEnum {

    FRAMEWORK(1),  // 框架内部错误
    EXT(2),  // 框架扩展错误
    BIZ(3),  // 业务错误
    THIRD_PARTY(
            4 // 第三方错误
    );
    private final int value;
}