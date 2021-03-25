package org.lan.iti.common.pay.exception;

import org.lan.iti.common.pay.enums.ErrorLevelEnum;
import org.lan.iti.common.pay.enums.ErrorTypeEnum;

/**
 * @author I'm
 * @since 2021/3/24
 * description
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = -6476522626904036567L;

    private final int code;

    private int level;

    public ServiceException(int code, String message) {
        super(message);
        this.code = code;
        this.level = ErrorLevelEnum.IGNORE.getValue();
    }

    public ServiceException(int code, int level, String message) {
        super(message);
        this.code = code;
    }

    public int getType() {
        return ErrorTypeEnum.BIZ.getValue();
    }

    public int getLevel() {
        return this.level;
    }

    public int getCode() {
        return this.code;
    }

    /**
     * 移除堆栈信息
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
