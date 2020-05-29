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

package org.lan.iti.common.data.orm.typehandler;

import org.postgresql.util.PGobject;

/**
 * Jsonb 类型转换器
 * <p>
 * 插入/更新时封装为PGobject对象
 * 查询时封装为PGobject对象后提取value字段
 * </p>
 *
 * @author NorthLan
 * @date 2020-04-10
 * @url https://noahlan.com
 */
public class StringJsonbTypeHandler extends BasePGJsonTypeHandler<String> {

    @Override
    protected String getType() {
        return JSONB;
    }

    @Override
    protected String convertValue(String parameter) {
        return parameter;
    }

    @Override
    protected String getValue(PGobject pGobject) {
        return pGobject.getValue();
    }
}
