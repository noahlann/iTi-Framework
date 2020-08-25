/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.common.core.base;


import cn.hutool.json.JSONUtil;
import lombok.Cleanup;
import org.lan.iti.common.core.constants.ITIConstants;
import org.lan.iti.common.core.enums.ErrorLevelEnum;
import org.lan.iti.common.core.enums.ErrorTypeEnum;
import org.lan.iti.common.core.enums.ITIExceptionEnum;
import org.lan.iti.common.core.error.ErrorCode;
import org.lan.iti.common.core.properties.ErrorProperties;
import org.lan.iti.common.model.response.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 错误页面的默认跳转(例如请求404的时候,默认走这个视图解析器)
 *
 * @author NorthLan
 * @date 2020-02-20
 * @url https://noahlan.com
 */
public class GlobalErrorView implements View {
    @Autowired
    private ErrorProperties properties;

    @Override
    public String getContentType() {
        return "text/html";
    }

    @Override
    public void render(@Nullable Map<String, ?> model, @Nullable HttpServletRequest request, @Nullable HttpServletResponse response) throws Exception {
        if (response == null) {
            return;
        }
        response.setCharacterEncoding(ITIConstants.UTF_8);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorCode.ErrorCodeBuilder errorCodeBuilder = ErrorCode.builder()
                .version(ITIConstants.EXCEPTION_ERROR_CODE_VERSION)
                .mark(properties.getMark())
                .type(ErrorTypeEnum.EXT.getValue())
                .level(ErrorLevelEnum.IMPORTANT.getValue())
                .code(ITIExceptionEnum.PAGE_NOT_FOUND.getCode());
        String errorCode = String.valueOf(ITIExceptionEnum.PAGE_NOT_FOUND.getCode());
        String msg = ITIExceptionEnum.PAGE_NOT_FOUND.getMsg();

        if (model != null && model.get("code") != null && model.get("message") != null) {
            errorCodeBuilder.code(Integer.parseInt((String) model.get("code")));
            errorCode = (String) model.get("code");
            msg = (String) model.get("message");
        } else {
            if (model != null && model.get("status") != null && model.get("error") != null) {
                errorCodeBuilder.code(Integer.parseInt((String) model.get("status")));
                errorCode = (String) model.get("status");
                msg = (String) model.get("error");
            }
        }
        @Cleanup PrintWriter writer = response.getWriter();
        writer.write(JSONUtil.toJsonStr(ApiResult.error(
                properties.isEnabled() ? errorCodeBuilder.build().toString() : errorCode,
                msg)));
    }
}
