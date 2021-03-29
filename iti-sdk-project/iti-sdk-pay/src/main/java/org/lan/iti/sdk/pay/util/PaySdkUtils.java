package org.lan.iti.sdk.pay.util;

import cn.hutool.core.bean.BeanUtil;
import lombok.experimental.UtilityClass;
import org.apache.http.HttpEntity;
import org.lan.iti.sdk.pay.model.DefaultResponse;
import org.lan.iti.sdk.pay.model.IResponse;

/**
 * @author I'm
 * @since 2021/3/27
 * description pay sdk util 一些sdk的基本方法
 */
@UtilityClass
public class PaySdkUtils {

    /**
     * 转换HttpEntity为IResponse
     *
     * @param httpEntity HttpEntity数据
     * @return 支付响应数据模型
     */
    public <R extends IResponse> DefaultResponse<R> httpEntity2DefaultResponse(HttpEntity httpEntity) {
        return BeanUtil.toBean(httpEntity, DefaultResponse.class);
    }

}
