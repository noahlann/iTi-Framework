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
public interface SignExceptionReason {

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    enum Configure implements IExceptionSpec {

        /**
         * 签名相关
         */
        ALGORITHM_NOT_FOUND("ALGORITHM_NOT_FOUND", "无此算法"),
        INVALID_PRIVATE_KEY("INVALID_PRIVATE_KEY", "私钥非法"),
        EMPTY_PRIVATE_KEY("EMPTY_PRIVATE_KEY", "私钥数据为空"),
        SIGN_VERIFY_FAIL("SIGN_VERIFY_FAIL", "签名验证失败"),
        SIGN_FAIL("SIGN_FAIL", "签名失败"),
        PARAM_PARSE_ERROR("PARAM_PARSE_ERROR", "参数解析异常"),
        ;

        private final String code;
        private final String message;

    }

}
