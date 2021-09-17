package org.lan.iti.sdk.pay.net;

import lombok.Data;

/**
 * @author I'm
 * @date 2021/9/16
 */
@Data
public class HttpResult {

    /**
     * http code
     */
    int code;

    /**
     * http data
     */
    String data;

}
