/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.common.data.tenant;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;

/**
 * 多租户处理器 (mp)
 * <p>
 * TenantId 行级
 *
 * @author NorthLan
 * @date 2020-02-24
 * @url https://noahlan.com
 */
@Slf4j
@AllArgsConstructor
public class ITITenantHandler implements TenantHandler {

    private final ITITenantProperties properties;

    /**
     * 获取租户值
     */
    @Override
    public Expression getTenantId(boolean where) {
        String tenantId = TenantContextHolder.getTenantId();
        log.debug("当前租户为：{}", tenantId);
        if (StrUtil.isBlank(tenantId)) {
            return new NullValue();
        }
        return new LongValue(tenantId);
    }

    /**
     * 获取租户字段
     */
    @Override
    public String getTenantIdColumn() {
        return properties.getColumn();
    }

    /**
     * 根据表名判断是否进行过滤
     */
    @Override
    public boolean doTableFilter(String tableName) {
        return !TenantContextHolder.hasTenant() || !properties.getTables().contains(tableName);
    }
}
