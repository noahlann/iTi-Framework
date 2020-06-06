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

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 逻辑删除 状态枚举
 * <pre>
 *     通常使用 del_flag 表示逻辑删除字段
 *     值为真：逻辑删除
 *     值为假：逻辑未删除
 * </pre>
 *
 * @author NorthLan
 * @date 2020-06-06
 * @url https://noahlan.com
 */
@Getter
@AllArgsConstructor
public enum LogicDeleteEnum {
    LOGIC_DELETE(1, true, "true", "逻辑删除值"),
    LOGIC_NOT_DELETE(0, false, "false", "逻辑未删除值");
    private Integer intValue;
    private Boolean boolValue;
    private String strValue;
    /**
     * 描述
     */
    private String desc;

    /**
     * 获取枚举
     *
     * @return 默认返回 逻辑未删除值
     */
    public LogicDeleteEnum getByInt(Integer value) {
        if (value == null) {
            return LOGIC_NOT_DELETE;
        }
        for (LogicDeleteEnum item : LogicDeleteEnum.values()) {
            if (item.intValue.equals(value)) {
                return item;
            }
        }
        return LOGIC_NOT_DELETE;
    }

    /**
     * 获取枚举
     *
     * @return 默认返回 逻辑未删除值
     */
    public LogicDeleteEnum getByBool(Boolean value) {
        if (value == null) {
            return LOGIC_NOT_DELETE;
        }
        for (LogicDeleteEnum item : LogicDeleteEnum.values()) {
            if (item.boolValue.equals(value)) {
                return item;
            }
        }
        return LOGIC_NOT_DELETE;
    }

    /**
     * 获取枚举
     *
     * @return 默认返回 逻辑未删除值
     */
    public LogicDeleteEnum getByStr(String value) {
        if (StrUtil.isBlank(value)) {
            return LOGIC_NOT_DELETE;
        }
        for (LogicDeleteEnum item : LogicDeleteEnum.values()) {
            if (item.strValue.equalsIgnoreCase(value)) {
                return item;
            }
        }
        return LOGIC_NOT_DELETE;
    }
}
