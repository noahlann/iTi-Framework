/*
 *
 *  * Copyright (c) [2019-2021] [NorthLan](lan6995@gmail.com)
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.lan.iti.common.core.api;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.lan.iti.common.core.exception.IExceptionSpec;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一的API返回结果类
 *
 * @author NorthLan
 * @date 2020-02-20
 * @url https://noahlan.com
 */
@SuppressWarnings("unused")
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(chain = true)
@ApiModel(description = "API返回结果类")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiResult<T> implements Serializable {
    private static final long serialVersionUID = 5984342895602543917L;

    /**
     * 返回编码
     */
    @ApiModelProperty("返回编码")
    private String code = null;

    /**
     * 消息描述以及说明
     * TODO 服务端多语言设定
     */
    @ApiModelProperty("消息描述以及说明")
    private String message = null;

    /**
     * 结果集
     */
    @ApiModelProperty("结果集：数据")
    private T data = null;

    /**
     * 预留扩展属性
     */
    @ApiModelProperty("预留扩展属性")
    private Map<String, Object> ext = new HashMap<>();

    // region Static-method

    /**
     * 复制ApiResult
     * <pre>
     *     data不进行复制，通常由用户自行转换
     * </pre>
     *
     * @param other 源
     * @param <T>   目标类型
     */
    public static <T> ApiResult<T> from(ApiResult<?> other) {
        return new ApiResult<T>()
                .setCode(other.getCode())
                .setMessage(other.getMessage())
                .setExt(other.getExt());
    }

    /**
     * 成功结果
     * <pre>
     *     数据与message均使用默认
     * </pre>
     */
    public static <T> ApiResult<T> ok() {
        return ok(null, null, null);
    }

    /**
     * 成功结果,无message
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return ApiResult
     */
    public static <T> ApiResult<T> ok(T data) {
        return ok(null, null, data);
    }

    /**
     * 成功结果
     * <pre>
     *     当T为CharSequence子类时，无法选择将参数赋值给data还是msg
     * </pre>
     *
     * @param message 成功描述信息
     */
    public static <T> ApiResult<T> ok(String message) {
        return ok(null, message, null);
    }

    /**
     * 成功结果
     *
     * @param data 数据域
     */
    public static <T> ApiResult<T> ok(T data, String message) {
        return ok(null, message, data);
    }

    /**
     * 成功结果
     *
     * @param data 数据域
     */
    public static <T> ApiResult<T> ok(String code, String message, T data) {
        ApiResult<T> result = new ApiResult<>();
        if (StrUtil.isBlank(message)) {
            message = DefaultEnum.SUCCESS.getMessage();
        }
        if (StrUtil.isBlank(code)) {
            code = DefaultEnum.SUCCESS.getCode();
        }
        return result
                .setMessage(message)
                .setCode(code)
                .setData(data);
    }

    /**
     * 失败结果
     *
     * @param code 错误码
     */
    public static <T> ApiResult<T> error(String code) {
        return error(code, null, null);
    }

    /**
     * 失败结果
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return ApiResult实例
     */
    public static <T> ApiResult<T> error(T data) {
        return error(null, null, data);
    }

    /**
     * 失败结果
     *
     * @param code    错误码
     * @param message 错误消息
     * @return ApiResult实例
     */
    public static <T> ApiResult<T> error(String code, String message) {
        return error(code, message, null);
    }

    /**
     * 失败结果
     * TODO 服务端多语言设定
     *
     * @param code    错误码
     * @param message 错误信息 必须重写 ToString
     * @param data    结果集，数据
     * @return ApiResult实例
     */
    public static <T> ApiResult<T> error(String code, String message, T data) {
        ApiResult<T> result = new ApiResult<>();

        if (StrUtil.isBlank(message)) {
            message = DefaultEnum.FAIL.getMessage();
        }
        if (StrUtil.isBlank(code)) {
            code = DefaultEnum.FAIL.getCode();
        }

        return result
                .setCode(code)
                .setMessage(message)
                .setData(data);
    }
    // endregion

    @SuppressWarnings("AlibabaEnumConstantsMustHaveComment")
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static enum DefaultEnum implements IExceptionSpec {
        SUCCESS("SUCCESS", "成功"),
        FAIL("FAIL", "失败"),
        ;
        private final String code;
        private final String message;
    }
}
