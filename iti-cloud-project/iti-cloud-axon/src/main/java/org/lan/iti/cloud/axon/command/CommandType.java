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

package org.lan.iti.cloud.axon.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.lan.iti.cloud.swagger.annotation.ApiEnum;
import org.lan.iti.common.core.support.IEnum;

/**
 * 命令类型
 *
 * @author NorthLan
 * @date 2021-06-02
 * @url https://noahlan.com
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ApiEnum(valueField = "code", descField = "message")
public enum CommandType implements IEnum<String> {
    DEFAULT("d", "默认"),
    CREATE("c", "创建"),
    ADD("a", "新增"),
    UPDATE("u", "更新"),
    DELETE("d", "删除"),
    ;
    private final String code;
    private final String message;
}
