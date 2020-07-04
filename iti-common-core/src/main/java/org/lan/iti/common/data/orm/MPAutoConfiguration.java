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

package org.lan.iti.common.data.orm;

import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.extension.parsers.DynamicTableNameParser;
import com.baomidou.mybatisplus.extension.parsers.ITableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import lombok.AllArgsConstructor;
import org.lan.iti.common.data.dynamic.DynamicTableHandler;
import org.lan.iti.common.data.dynamic.DynamicTableProperties;
import org.lan.iti.common.data.tenant.ITITenantHandler;
import org.lan.iti.common.data.tenant.ITITenantProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author NorthLan
 * @date 2020-03-20
 * @url https://noahlan.com
 */
@Configuration
@MapperScan(basePackages = {"org.lan.iti.**.mapper", "com.gxzc.**.mapper"})
@ConditionalOnClass(ITIMetaObjectHandler.class)
@EnableConfigurationProperties({ITITenantProperties.class, DynamicTableProperties.class})
@AllArgsConstructor
public class MPAutoConfiguration {

    /**
     * 租户配置
     */
    private final ITITenantProperties tenantProperties;

    /**
     * 动态表名配置
     */
    private final DynamicTableProperties dynamicTableProperties;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "iti.tenant", name = "enabled", havingValue = "true")
    public ITITenantHandler itiTenantHandler() {
        return new ITITenantHandler(tenantProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor interceptor = new PaginationInterceptor();
        List<ISqlParser> sqlParserList = new ArrayList<>();
        if (tenantProperties.isEnabled()) {
            TenantSqlParser tenantSqlParser = new TenantSqlParser();
            tenantSqlParser.setTenantHandler(itiTenantHandler());
            sqlParserList.add(tenantSqlParser);
        }

        if (dynamicTableProperties.isEnabled()) {
            DynamicTableNameParser dynamicTableNameParser = new DynamicTableNameParser();
            Map<String, ITableNameHandler> tableNameHandlerMap = new HashMap<>();
            for (DynamicTableProperties.TableNameDict dict : dynamicTableProperties.getNames()) {
                tableNameHandlerMap.put(dict.getFullName(), new DynamicTableHandler(dict));
            }
            dynamicTableNameParser.setTableNameHandlerMap(tableNameHandlerMap);
            sqlParserList.add(dynamicTableNameParser);
        }

        interceptor.setSqlParserList(sqlParserList);
        return interceptor;
    }
}
