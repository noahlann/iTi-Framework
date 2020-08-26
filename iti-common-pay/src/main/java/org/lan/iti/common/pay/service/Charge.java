package org.lan.iti.common.pay.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.util.ValidationUtils;
import org.lan.iti.common.pay.config.PayConfig;
import org.lan.iti.common.pay.model.PayModel;
import org.lan.iti.common.pay.util.PayUtils;
import org.springframework.validation.BindException;

import java.util.Map;

/**
 * @author I'm
 * @since 2020/8/25
 * description 支付凭据
 */
@UtilityClass
@Slf4j
public class Charge {

    private Map<String, Object> config;

    public void setOptions(PayConfig payConfig) throws IllegalArgumentException, IllegalAccessException {
        config = PayModel.buildMap(payConfig);
    }

    public String createCharge(Map<String, Object> map) throws BindException {
        ValidationUtils.validate(config, (Object) null);
        map.put("appId",config.get("appId"));
        String sign = PayUtils.sign(PayUtils.parseSignContent(map), Convert.toStr(getConfig("privateKey")));
        HttpRequest request = HttpUtil.createPost(Convert.toStr(getConfig("gatewayHost")) + "?" + "&appId=" + Convert.toStr(getConfig("appId")));
        request.contentType("application/json");
        map.put("sign", sign);
        try {
            request.body(new ObjectMapper().writeValueAsString(map));
        } catch (JsonProcessingException jsonProcessingException) {
            log.error(jsonProcessingException.getMessage(), jsonProcessingException);
            throw new RuntimeException(jsonProcessingException.getMessage(), jsonProcessingException);
        }
        return request.execute(true).body();
    }

    private String getConfig(String key) {
        return Convert.toStr(config.get(key));
    }

}
