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

package org.lan.iti.cloud.strategy;

import lombok.extern.slf4j.Slf4j;
import org.lan.iti.cloud.strategy.exception.StrategyTimeoutException;
import org.lan.iti.cloud.strategy.meta.StrategyMetaIndexer;
import org.lan.iti.cloud.strategy.meta.StrategyMeta;
import org.lan.iti.cloud.support.NamedThreadFactory;
import org.slf4j.MDC;

import javax.validation.constraints.NotNull;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 扩展点的动态代理.
 *
 * @author NorthLan
 * @date 2021-02-24
 * @url https://noahlan.com
 */
@Slf4j
public class StrategyInvocationHandler<Strategy extends IStrategy, R> implements InvocationHandler {
    private static final ExecutorService STRATEGY_INVOKE_TIMER_EXECUTOR = new ThreadPoolExecutor(
            10,
            System.getProperty("invokeStrategyMaxPoolSize") != null ? Integer.parseInt(System.getProperty("invokeExtMaxPoolSize")) : 50,
            // 线程5m内idle，则被回收
            5L, TimeUnit.MINUTES,
            // 无队列，线程池满后新请求进来则 RejectedExecutionException
            new SynchronousQueue<>(),
            // daemon=false, shutdown时等待扩展点执行完毕
            new NamedThreadFactory("StrategyInvokeTimer", false));

    private final Class<Strategy> strategyInterface;
    private final Object params;
    private final Strategy defaultStrategy;
    private final int timeoutInMs;

    StrategyInvocationHandler(@NotNull Class<Strategy> strategyInterface, @NotNull Object params, Strategy defaultStrategy, int timeoutInMs) {
        this.strategyInterface = strategyInterface;
        this.params = params;
        this.defaultStrategy = defaultStrategy;
        this.timeoutInMs = timeoutInMs;
    }

    @SuppressWarnings("unchecked")
    Strategy createProxy() {
        return (Strategy) Proxy.newProxyInstance(strategyInterface.getClassLoader(), new Class[]{this.strategyInterface}, this);
    }

    @Override
    public Object invoke(Object proxy, final Method method, Object[] args) throws Throwable {
        List<StrategyMeta> effectiveStrategies = StrategyMetaIndexer.findEffectiveStrategies(strategyInterface, params, false);
        log.debug("{} effective {}", strategyInterface.getCanonicalName(), effectiveStrategies);

//        if (effectiveStrategies.isEmpty()) {
//            if (defaultExt == null) {
//                log.debug("found NO ext instance {} on {}, HAS TO return null", extInterface.getCanonicalName(), params);
//                throw StrategyNotFoundException.ofMessage("found NO strategy instance {} on {}, HAS TO return null",
//                        extInterface.getCanonicalName(), params);
//                // 具体策略方法的返回值不能是int/boolean等，否则会抛出NPE!
////                return null;
//            }
//
//            log.debug("use default {}", defaultExt);
//            effectiveStrategies.add(new StrategyMeta(defaultExt));
//        }

        // all effective extension instances found
        List<R> accumulatedResults = new ArrayList<>(effectiveStrategies.size());
        R result = null;
        for (StrategyMeta strategyMeta : effectiveStrategies) {
            result = invokeStrategy(strategyMeta, method, args);
            accumulatedResults.add(result);

            // TODO 匹配到多个策略实现的处理方式（已排序）
        }
        return result;
    }

    private R invokeStrategy(StrategyMeta strategyMeta, final Method method, Object[] args) throws Throwable {
        try {
            return invokeStrategyMethod(strategyMeta, method, args);
        } catch (InvocationTargetException e) {
            // 此处接收被调用方法内部未被捕获的异常：扩展点里抛出异常
            log.error("{} code:{}", this.strategyInterface.getCanonicalName(), strategyMeta.getCode(), e.getTargetException());
            throw e.getTargetException();
        } catch (TimeoutException e) {
            log.error("timed out:{}ms, {} method:{} args:{}", timeoutInMs, strategyMeta.getStrategyBean(), method.getName(), args);
            // java里的TimeoutException继承Exception，需要转为ExtTimeoutException，否则上层看到的异常是 UndeclaredThrowableException
            throw new StrategyTimeoutException(timeoutInMs);
        } catch (RejectedExecutionException e) {
            // TODO 需要加日志报警
            log.error("ExtInvokeTimer thread pool FULL:{}", e.getMessage());
            throw e;
        } catch (Throwable e) {
            // should never happen
            log.error("{} code:{} unexpected", this.strategyInterface.getCanonicalName(), strategyMeta.getCode(), e);
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    private R invokeStrategyMethod(StrategyMeta strategyMeta, Method method, Object[] args) throws Throwable {
        IStrategy extInstance = strategyMeta.getStrategyBean();
        if (timeoutInMs > 0) {
            return invokeExtensionMethodWithTimeout(extInstance, method, args, timeoutInMs);
        }

        R result = (R) method.invoke(extInstance, args);
        log.debug("{} method:{} args:{}, result:{}", extInstance, method.getName(), args, result);

        return result;
    }

    @SuppressWarnings("unchecked")
    private R invokeExtensionMethodWithTimeout(IStrategy instance, Method method, Object[] args, final int timeoutInMs) throws Throwable {
        // 切换到线程池ThreadLocal会失效，目前ThreadLocal只有MDC
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        Future<R> future = STRATEGY_INVOKE_TIMER_EXECUTOR.submit(() -> {
            MDC.setContextMap(mdcContext); // 手动继承前面线程的MDC
            try {
                return (R) method.invoke(instance, args);
            } finally {
                MDC.clear();
            }
        });

        try {
            R result = future.get(timeoutInMs, TimeUnit.MILLISECONDS);
            log.debug("{} method:{} args:{}, result:{}", instance, method.getName(), args, result);

            return result;
        } catch (TimeoutException e) {
            if (!future.isCancelled()) {
                // best effort
                future.cancel(true);
            }

            throw e;
        } catch (ExecutionException e) {
            // future的异常机制，这里尽可能把真实的异常抛出去
            throw e.getCause() != null ? e.getCause() : e;
        }
    }
}
