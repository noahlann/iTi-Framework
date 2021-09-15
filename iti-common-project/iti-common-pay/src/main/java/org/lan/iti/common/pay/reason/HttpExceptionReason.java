package org.lan.iti.common.pay.reason;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.lan.iti.common.core.exception.IExceptionSpec;

/**
 * @author I'm
 * @date 2021/6/11
 * description
 */
public interface HttpExceptionReason {

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    enum Configure implements IExceptionSpec {

        /**
         * http 相关
         */
        HTTP_ERROR("HTTP_ERROR", "http请求异常"),
        ;

        private final String code;
        private final String message;

    }

}
