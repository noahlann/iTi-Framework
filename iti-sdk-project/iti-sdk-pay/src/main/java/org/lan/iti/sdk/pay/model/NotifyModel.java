package org.lan.iti.sdk.pay.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author I'm
 * @date 2021/6/25
 * description 异步通知结构
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class NotifyModel {

    /**
     * 通知签名
     */
    protected String signature;

    /**
     * 异步通知地址
     */
    protected String notifyUrl;

}
