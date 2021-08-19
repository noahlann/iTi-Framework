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

package org.lan.iti.cloud.handler;

import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;
import org.lan.iti.common.core.api.ApiResult;
import org.lan.iti.common.core.util.StringPool;

import java.sql.SQLException;

/**
 * @author NorthLan
 * @date 2021-04-29
 * @url https://noahlan.com
 */
@UtilityClass
public class ExceptionHandlerHelper {
    static ApiResult<String> sqlException(SQLException e, String msgPrefix) {
        if (StrUtil.isEmpty(msgPrefix)) {
            msgPrefix = "数据库错误";
        }
        return ApiResult.error(String.valueOf(e.getErrorCode()),
                msgPrefix + StringPool.COLON + e.getSQLState(), e.getLocalizedMessage());
    }
}
