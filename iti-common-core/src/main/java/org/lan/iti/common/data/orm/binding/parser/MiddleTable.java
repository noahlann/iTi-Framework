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

package org.lan.iti.common.data.orm.binding.parser;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 中间表
 *
 * @author NorthLan
 * @date 2020-04-18
 * @url https://noahlan.com
 */
@Slf4j
public class MiddleTable {
    /**
     * 中间表
     */
    private String table;
    /**
     * 与注解VO的外键关联的连接字段
     */
    private String equalsToAnnoObjectFKColumn;
    /**
     * 与被引用Entity属性主键的连接字段
     */
    private String equalsToRefEntityPkColumn;
    /**
     * 附加条件
     */
    private List<String> additionalConditions;

    public MiddleTable(String table){
        this.table = table;
    }
}
