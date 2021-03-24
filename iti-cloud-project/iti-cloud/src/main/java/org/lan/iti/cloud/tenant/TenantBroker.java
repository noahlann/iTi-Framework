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

package org.lan.iti.cloud.tenant;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * 租户运行时代理
 * <p>
 * 这是一个工具类，用于切换租户运行时，保护租户ID上下文<br/>
 * 下面这段代码演示问题所在 <pre>
 *     void methodA(){
 *         // 因为某些特殊原因，需要手动指定租户
 *         TenantContextHolder.setTenantId("1");
 *         // do something ...
 *     }
 *     void methodB(){
 *         // 因为某些特殊原因，需要手动指定租户
 *         TenantContextHolder.setTenantId("1");
 *         methodA();
 *         // 此时租户ID已经变成 1
 *         // do something ...
 *     }
 * </pre> 嵌套设置租户ID会导致租户上下文难以维护,并且很难察觉，容易导致数据错乱。 推荐的写法： <pre>
 *     void methodA(){
 *         TenantBroker.RunAs(1,() -> {
 *             // do something ...
 *         });
 *     }
 *     void methodB(){
 *         TenantBroker.RunAs(2,() -> {
 *              methodA();
 *             // do something ...
 *         });
 *     }
 * </pre>
 *
 * @author NorthLan
 * @date 2021-03-10
 * @url https://noahlan.com
 */
@Slf4j
@UtilityClass
public class TenantBroker {

    @FunctionalInterface
    public interface RunAs<T> {
        /**
         * 业务逻辑执行
         *
         * @param tenantId 租户ID
         * @throws Exception 可能发生的异常
         */
        void run(T tenantId) throws Exception;
    }

    @FunctionalInterface
    public interface ApplyAs<T, R> {
        /**
         * 业务逻辑执行,具有返回值
         *
         * @param tenantId 租户ID
         * @return 返回值
         * @throws Exception 可能发生的异常
         */
        R apply(T tenantId) throws Exception;
    }

    public static class TenantBrokerExceptionWrapper extends RuntimeException {
        public TenantBrokerExceptionWrapper(String message, Throwable cause) {
            super(message, cause);
        }

        public TenantBrokerExceptionWrapper(Throwable cause) {
            super(cause);
        }
    }

    /**
     * 以某个租户身份运行
     *
     * @param tenantId 租户ID
     * @param runAs    业务逻辑
     */
    public void runAs(String tenantId, RunAs<String> runAs) {
        final String pre = TenantContextHolder.getTenantId();
        try {
            log.trace("TenantBroker 切换租户 {} -> {}", pre, tenantId);
            TenantContextHolder.setTenantId(tenantId);
            runAs.run(tenantId);
        } catch (Exception e) {
            throw new TenantBrokerExceptionWrapper(e.getMessage(), e);
        } finally {
            log.trace("TenantBroker 还原租户 {} <- {}", pre, tenantId);
            TenantContextHolder.setTenantId(pre);
        }
    }

    /**
     * 以某个租户身份允许，有返回值
     *
     * @param tenantId 租户ID
     * @param applyAs  业务逻辑
     * @param <T>      返回值类型
     * @return 业务返回值
     */
    public <T> T applyAs(String tenantId, ApplyAs<String, T> applyAs) {
        final String pre = TenantContextHolder.getTenantId();
        try {
            log.trace("TenantBroker 切换租户 {} -> {}", pre, tenantId);
            TenantContextHolder.setTenantId(tenantId);
            return applyAs.apply(tenantId);
        } catch (Exception e) {
            throw new TenantBrokerExceptionWrapper(e.getMessage(), e);
        } finally {
            log.trace("TenantBroker 还原租户 {} <- {}", pre, tenantId);
            TenantContextHolder.setTenantId(pre);
        }
    }

    /**
     * 以某个租户身份运行
     *
     * @param tenantSupplier 获取租户ID的方法
     * @param runAs          业务逻辑
     */
    public void runAs(Supplier<String> tenantSupplier, RunAs<String> runAs) {
        runAs(tenantSupplier.get(), runAs);
    }

    /**
     * 以某个租户身份允许，有返回值
     *
     * @param tenantSupplier 获取租户ID的方法
     * @param applyAs        业务逻辑
     * @param <T>            业务返回值类型
     * @return 业务返回值
     */
    public <T> T applyAs(Supplier<String> tenantSupplier, ApplyAs<String, T> applyAs) {
        return applyAs(tenantSupplier.get(), applyAs);
    }
}
