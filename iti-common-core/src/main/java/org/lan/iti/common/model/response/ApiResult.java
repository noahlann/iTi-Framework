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

package org.lan.iti.common.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.lan.iti.common.core.enums.StatusEnum;

import java.io.Serializable;

/**
 * 统一回复消息体
 *
 * @author NorthLan
 * @date 2020-02-20
 * @url https://noahlan.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(chain = true)
@ApiModel(description = "响应信息主体")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult<T> implements Serializable {
    private static final long serialVersionUID = 5984342895602543917L;

    /**
     * 状态码
     * {@link StatusEnum}
     * <p>
     * 框架：成功标记=1，失败标记=0
     * </p>
     */
    @ApiModelProperty(value = "返回标记：成功标记=1，失败标记=0")
    private Integer status = StatusEnum.SUCCESS.getCode();

    /**
     * 错误码，当且仅当status=0时有效
     */
    @ApiModelProperty(value = "错误码：当且仅当status=0时有效")
    private Object errorCode = null;

    /**
     * 消息描述以及说明
     * TODO 服务端多语言设定
     */
    @ApiModelProperty(value = "消息描述以及说明")
    private Object msg = "成功";

    /**
     * 结果集
     */
    @ApiModelProperty(value = "结果集：数据")
    private T data = null;

    // region Static-method

    /**
     * 成功结果
     */
    public static <T> ApiResult<T> ok() {
        return ok(null, null);
    }

    /**
     * 成功结果
     *
     * @param data 数据域
     */
    public static <T> ApiResult<T> ok(T data) {
        return ok(null, data);
    }

    /**
     * 成功结果
     *
     * @param msg 成功描述信息
     */
    public static <T> ApiResult<T> ok(CharSequence msg) {
        return ok(msg, null);
    }

    /**
     * 成功结果
     * TODO 服务端多语言设定
     *
     * @param msg  成功描述信息，必须重写ToString方法
     * @param data 结果集，数据
     */
    public static <T> ApiResult<T> ok(Object msg, T data) {
        ApiResult<T> result = new ApiResult<>();
        if (msg == null) {
            msg = "成功";
        }
        return result.setMsg(msg).setData(data);
    }

    /**
     * 失败结果
     *
     * @param errorCode 错误码
     */
    public static <T> ApiResult<T> error(Object errorCode) {
        return error(errorCode, null, null);
    }

    /**
     * 失败结果
     *
     * @param errorCode 错误码
     * @param errorMsg  错误消息
     */
    public static <T> ApiResult<T> error(Object errorCode, CharSequence errorMsg) {
        return error(errorCode, errorMsg, null);
    }

    /**
     * 失败结果
     * TODO 服务端多语言设定
     *
     * @param errorCode 错误码
     * @param errorMsg  错误信息 必须重写 ToString
     * @param data      结果集，数据
     */
    public static <T> ApiResult<T> error(Object errorCode, Object errorMsg, T data) {
        ApiResult<T> result = new ApiResult<>();
        if (errorMsg == null) {
            errorMsg = "失败";
        }
        return result
                .setStatus(StatusEnum.FAIL.getCode())
                .setErrorCode(errorCode)
                .setMsg(errorMsg)
                .setData(data);
    }

    /**
     * 空消息体,不建议常用
     * 默认为成功
     */
    public static <T> ApiResult<T> empty() {
        return new ApiResult<>();
    }

    // endregion
}
