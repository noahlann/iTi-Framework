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

import cn.hutool.json.JSONUtil;
import org.postgresql.util.PGobject;

import java.util.Map;

/**
 * Map <-> Jsonb 类型处理器
 *
 * @author NorthLan
 * @date 2020-05-28
 * @url https://noahlan.com
 */
public class MapJsonbTypeHandler extends BasePGJsonTypeHandler<Map> {

    @Override
    protected String getType() {
        return JSONB;
    }

    @Override
    protected String convertValue(Map parameter) {
        return JSONUtil.toJsonStr(parameter);
    }

    @Override
    protected Map getValue(PGobject pGobject) {
        return JSONUtil.toBean(pGobject.getValue(), Map.class);
    }
}
