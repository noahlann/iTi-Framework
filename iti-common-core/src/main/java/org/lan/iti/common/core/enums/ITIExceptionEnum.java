/*
 *
 *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
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

package org.lan.iti.common.core.enums;

import lombok.Getter;

/**
 * 框架错误枚举
 * <pre>
 *     100-511 为Http状态,尽量不使用避免混淆
 *     600-999 为框架预留
 *     600-619 参数错误
 *     620-639 致命错误
 *     640-999 待定
 * </pre>
 * TODO 框架多语言
 *
 * @author NorthLan
 * @date 2020-02-22
 * @url https://noahlan.com
 */
@Getter
public enum ITIExceptionEnum {
    /**
     * 参数错误 600开始
     */
    METHOD_ARGUMENT_NOT_VALID(600, "请求参数验证错误"),
    CHECK_ERROR("参数检查错误"),
    PAGE_NOT_FOUND("页面不存在"),
    BAD_SQL("SQL语句错误"),

    /**
     * 其他错误
     */
    INTERNAL_SERVER_ERROR(620, "服务端错误"),

    BIZ_INSERT_ERROR(800, "插入数据错误"),
    BIZ_DELETE_ERROR("删除数据错误"),
    BIZ_SELECT_ERROR("查询数据错误"),
    BIZ_UPDATE_ERROR("更新数据错误"),
    ;

    private int code;
    private String msg;

    ITIExceptionEnum(String msg) {
        this(Counter.nextValue, msg);
    }

    ITIExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        Counter.nextValue = code + 1;
    }

    private static class Counter {
        private static int nextValue = 0;
    }
}
