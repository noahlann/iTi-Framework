package org.lan.iti.sdk.pay.model;

import cn.hutool.core.convert.Convert;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author I'm
 * @since 2021/3/27
 * description 业务统一响应参数
 */
@Data
public class DefaultResponse<R extends IResponse> {

    private String code;

    private String message;

    private R response;

    private List<R> responseList;

    private Object error;

    private Map<String, Object> extra;

    public DefaultResponse(int code, String message, Object error) {
        this.code = Convert.toStr(code);
        this.message = message;
        this.error = error;
    }

    public DefaultResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public DefaultResponse(String code, String message, R response) {
        this.code = code;
        this.message = message;
        this.response = response;
    }

    public DefaultResponse(String code, String message, List<R> responseList) {
        this.code = code;
        this.message = message;
        this.responseList = responseList;
    }

}
