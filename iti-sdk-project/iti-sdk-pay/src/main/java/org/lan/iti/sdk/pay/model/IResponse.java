package org.lan.iti.sdk.pay.model;

import java.util.Map;

/**
 * @author I'm
 * @since 2021/3/26
 * description 响应参数基类
 */
public interface IResponse extends IModel {

    /**
     * 获取 平台 app id
     *
     * @return 平台 app id 数据
     */
    String getAppId();

    /**
     * 获取 额外字段
     *
     * @return 额外字段
     */
    Map<String, Object> getExtra();

}
