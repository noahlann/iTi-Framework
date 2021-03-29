package org.lan.iti.sdk.pay.model;

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

    private String msg;

    private R response;

    private List<R> responseList;

    private Map<String, Object> extra;

    public DefaultResponse(R response) {
        this.response = response;
    }

    public DefaultResponse(List<R> responseList) {
        this.responseList = responseList;
    }

}
