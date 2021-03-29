package org.lan.iti.sdk.pay.validate;

import org.lan.iti.common.pay.constants.PayFieldNameConstants;

/**
 * @author I'm
 * @since 2021/3/27
 * description 接口网关验证器
 */
public class GatewayHostValidate implements Validate {

    @Override
    public String getName() {
        return PayFieldNameConstants.GATEWAY_HOST;
    }

    @Override
    public boolean supports(String key) {
        return false;
    }

    @Override
    public boolean emptyValidate(Object value) {
        return false;
    }

    @Override
    public boolean patternValidate(Object value) {
        return false;
    }

}
