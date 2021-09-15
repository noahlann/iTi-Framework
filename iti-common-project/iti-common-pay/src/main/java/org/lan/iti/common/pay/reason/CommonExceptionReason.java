package org.lan.iti.common.pay.reason;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.lan.iti.common.core.exception.IExceptionSpec;

/**
 * 未分类异常
 *
 * @author I'm
 * @date 2021/9/14
 */
public interface CommonExceptionReason {

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    enum SYSTEM implements IExceptionSpec {

        PARAM_PARSE_FAIL("PARAM_PARSE_FAIL", "参数解析失败"),
        INVALID_GATEWAY_HOST("INVALID_GATEWAY_HOST", "请求地址不可用"),
        ;

        private final String code;
        private final String message;

    }

}
