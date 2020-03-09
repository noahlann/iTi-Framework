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
import org.lan.iti.common.model.response.ApiResult;
import org.lan.iti.common.core.constants.ITIConstants;
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

        int errorCode = 404; // TODO 读取enum
        String msg = "请求页面不存在"; // TODO 读取enum

        if (model != null && model.get("code") != null && model.get("message") != null) {
            errorCode = Integer.parseInt((String) model.get("code"));
            msg = (String) model.get("message");
        } else {
            if (model != null && model.get("status") != null && model.get("error") != null) {
                errorCode = Integer.parseInt((String) model.get("status"));
                msg = (String) model.get("error");
            }
        }
        @Cleanup PrintWriter writer = response.getWriter();
        writer.write(JSONUtil.toJsonStr(ApiResult.error(errorCode, msg)));
    }
}
