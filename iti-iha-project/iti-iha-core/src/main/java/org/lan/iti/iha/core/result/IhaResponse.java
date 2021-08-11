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

package org.lan.iti.iha.core.result;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class IhaResponse extends HashMap<String, Object> {
    private static final long serialVersionUID = -8775916040068466418L;
    /**
     * 消息码
     * <p>自定义消息码需 >= 600</p>
     */
    public final static String KEY_CODE = "code";

    /**
     * 本次请求描述
     */
    public final static String KEY_MESSAGE = "message";

    /**
     * 数据集
     * <p>可单可一</p>
     */
    public final static String KEY_DATA = "data";

    /**
     * Error
     */
    public final static String KEY_ERROR = "error";
    // region Static-method

    /**
     * 复制IhaResponse
     * <pre>
     *     data不进行复制，通常由用户自行转换
     * </pre>
     *
     * @param other 源
     */
    public static IhaResponse from(IhaResponse other) {
        IhaResponse result = new IhaResponse();
        result.putMap(other);
        return result;
    }

    /**
     * 融合多个IhaResponse，后值覆盖先值
     *
     * @param values 待融合值
     */
    public static IhaResponse merge(IhaResponse... values) {
        IhaResponse result = new IhaResponse();
        for (IhaResponse value : values) {
            result.putMap(value);
        }
        return result;
    }

    /**
     * 创建空消息体
     */
    public static IhaResponse empty() {
        return new IhaResponse();
    }

    /**
     * 成功结果
     * <pre>
     *     数据与message均使用默认
     * </pre>
     */
    public static IhaResponse ok() {
        return ok(null, null, null);
    }

    /**
     * 成功结果,无message
     *
     * @param data 数据
     * @return IhaResponse
     */
    public static IhaResponse ok(Object data) {
        return ok(null, null, data);
    }

    /**
     * 成功结果
     *
     * @param data 数据域
     */
    public static IhaResponse ok(Object data, String message) {
        return ok(null, message, data);
    }

    /**
     * 成功结果
     *
     * @param data 数据域
     */
    public static IhaResponse ok(Integer code, String message, Object data) {
        IhaResponse result = new IhaResponse();
        if (StrUtil.isEmpty(message)) {
            message = IhaResponseCode.SUCCESS.getMessage();
        }
        if (code == null) {
            code = IhaResponseCode.SUCCESS.getCode();
        }
        return result
                .message(message)
                .code(code)
                .data(data);
    }

    /**
     * 失败结果
     *
     * @param code 错误码
     */
    public static IhaResponse error(Integer code) {
        return error(code, null, null);
    }

    /**
     * 失败结果
     *
     * @param message 错误消息
     * @return IhaResponse实例
     */
    public static IhaResponse error(String message) {
        return error(null, message, null);
    }

    /**
     * 失败结果
     *
     * @param code    错误码
     * @param message 错误消息
     * @return IhaResponse实例
     */
    public static IhaResponse error(Integer code, String message) {
        return error(code, message, null);
    }

    /**
     * 失败结果
     *
     * @param code 错误码
     */
    public static IhaResponse error(IhaResponseCode code) {
        return error(code.getCode(), code.getMessage(), null);
    }

    /**
     * 失败结果
     *
     * @param code    错误码
     * @param message 错误信息 必须重写 ToString
     * @param data    结果集，数据
     * @return IhaResponse实例
     */
    public static IhaResponse error(Integer code, String message, Object data) {
        IhaResponse result = new IhaResponse();

        if (StrUtil.isBlank(message)) {
            message = IhaResponseCode.ERROR.getMessage();
        }
        if (code == null) {
            code = IhaResponseCode.ERROR.getCode();
        }

        return result
                .code(code)
                .message(message)
                .data(data);
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException("Not supported: remove(Object)");
    }

    @Override
    public boolean remove(Object key, Object value) {
        throw new UnsupportedOperationException("Not supported: remove(Object, Object)");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported: clear");
    }

    @Override
    @NotNull
    public IhaResponse put(String key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        super.put(key, value);
        return this;
    }

    @Override
    @Deprecated
    public void putAll(Map<? extends String, ?> m) {
        throw new UnsupportedOperationException("Deprecated: method putAll(Map) in IhaResponse is forbidden. Use putMap(Map) instead.");
    }

    public IhaResponse putMap(Map<? extends String, ?> m) {
        if (m == null) {
            throw new IllegalArgumentException("map cannot be null");
        }
        super.putAll(m);
        return this;
    }

    // region Methods
    public IhaResponse code(Integer code) {
        if (code == null) {
            throw new IllegalArgumentException("code cannot be null");
        }
        this.put(KEY_CODE, code);
        return this;
    }

    public IhaResponse message(String message) {
        this.put(KEY_MESSAGE, message);
        return this;
    }

    public IhaResponse data(Object data) {
        this.put(KEY_DATA, data);
        return this;
    }
    // endregion


    /**
     * 消息码
     */
    public Integer getCode() {
        return getByKey(KEY_CODE, IhaResponseCode.SUCCESS.getCode());
    }

    /**
     * 本次请求描述
     */
    public String getMessage() {
        return getByKey(KEY_MESSAGE, IhaResponseCode.SUCCESS.getMessage());
    }

    /**
     * 获取数据集
     */
    public <T> T getData() {
        return getByKey(KEY_DATA);
    }

    // utils
    public <T> T getByKey(String key) {
        return getByKey(key, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T getByKey(String key, T defaultValue) {
        return getByKey(key, defaultValue, obj -> (T) obj);
    }

    @SuppressWarnings("unchecked")
    public <T> T getByKey(String key, T defaultValue, Function<Object, T> cast) {
        Object obj = this.get(key);
        if (obj == null) {
            return defaultValue;
        }
        if (cast == null) {
            return (T) obj;
        }
        return cast.apply(obj);
    }
    // endregion

    /**
     * Whether the result currently returned is successful
     *
     * @return boolean
     */
    public boolean isSuccess() {
        return this.getCode() == HttpStatus.HTTP_OK;
    }

    /**
     * Methods provided for social, oauth, and oidc login strategy. The business flow is as follows:
     * <p>
     * 1. When not logged in, call the <code>strategy.authenticate(x)</code> method,
     * and the returned <code>IhaResponse.data</code> is the authorize url of the third-party platform
     * <p>
     * 2. When the third-party login completes the callback, call the <code>strategy.authenticate(x)</code> method,
     * and the returned <code>IhaResponse.data</code> is iha user
     * <p>
     * After calling the <code>strategy.authenticate(x)</code> method,
     * the developer can use <code>ihaResponse.isRedirectUrl()</code> to determine whether the current processing result
     * is the authorize url that needs to be redirected, or the iha user after successful login
     * <p>
     *
     * @return boolean
     */
    public boolean isRedirectUrl() {
        Object data = this.getData();
        return isSuccess()
                && ObjectUtil.isNotNull(data)
                && data instanceof String
                && ((String) data).startsWith("http");
    }
}
