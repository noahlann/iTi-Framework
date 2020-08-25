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

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 条件表达式管理器
 *
 * @author NorthLan
 * @date 2020-04-18
 * @url https://noahlan.com
 */
@Slf4j
@UtilityClass
public class ConditionManager {
    /**
     * 表达式缓存Map
     */
    private final Map<String, List<Expression>> expressionParseResultMap = new ConcurrentHashMap<>();

    /**
     * 获取解析后的Expression列表
     */
    private List<Expression> getExpressionList(String condition) {
        if (StringUtils.isEmpty(condition)) {
            return null;
        }
        List<Expression> expressionList = expressionParseResultMap.get(condition);
        if (expressionList == null) {
            ConditionParser visitor = new ConditionParser();
            try {
                Expression expression = CCJSqlParserUtil.parseCondExpression(condition);
                expression.accept(visitor);
                expressionList = visitor.getExpressList();
                expressionParseResultMap.put(condition, expressionList);
            } catch (Exception e) {
                log.error("关联条件解析异常", e);
            }
        }
        return expressionList;
    }
}
