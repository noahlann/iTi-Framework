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

package org.lan.iti.cloud.ddd.runtime;

import lombok.extern.slf4j.Slf4j;
import org.lan.iti.cloud.ddd.runtime.registry.ExtensionMeta;
import org.lan.iti.cloud.ddd.runtime.registry.InternalIndexer;
import org.lan.iti.common.core.util.Formatter;
import org.lan.iti.common.ddd.ext.IDomainExtension;
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
public class ExtensionInvocationHandler<Ext extends IDomainExtension, R> implements InvocationHandler {
    private static final ExecutorService EXT_INVOKE_TIMER_EXECUTOR = new ThreadPoolExecutor(
            10,
            System.getProperty("invokeExtMaxPoolSize") != null ? Integer.parseInt(System.getProperty("invokeExtMaxPoolSize")) : 50,
            // 线程5m内idle，则被回收
            5L, TimeUnit.MINUTES,
            // 无队列，线程池满后新请求进来则 RejectedExecutionException
            new SynchronousQueue<>(),
            // daemon=false, shutdown时等待扩展点执行完毕
            new NamedThreadFactory("ExtInvokeTimer", false));

    private final Class<Ext> extInterface;
    private final Object params;
    private final IReducer<R> reducer;
    private final Ext defaultExt;
    private final int timeoutInMs;

    ExtensionInvocationHandler(@NotNull Class<Ext> extInterface, @NotNull Object params, IReducer<R> reducer, Ext defaultExt, int timeoutInMs) {
        this.extInterface = extInterface;
        this.params = params;
        this.reducer = reducer;
        this.defaultExt = defaultExt;
        this.timeoutInMs = timeoutInMs;
    }

    @SuppressWarnings("unchecked")
    Ext createProxy() {
        return (Ext) Proxy.newProxyInstance(extInterface.getClassLoader(), new Class[]{this.extInterface}, this);
    }

    @Override
    public Object invoke(Object proxy, final Method method, Object[] args) throws Throwable {
        List<ExtensionMeta> effectiveExts = InternalIndexer.findEffectiveExtensions(extInterface, params, reducer == null);
        log.debug("{} effective {}", extInterface.getCanonicalName(), effectiveExts);

        if (effectiveExts.isEmpty()) {
            if (defaultExt == null) {
                log.debug("found NO ext instance {} on {}, HAS TO return null", extInterface.getCanonicalName(), params);
                throw new ExtNotFoundException(Formatter.format("found NO ext instance {} on {}, HAS TO return null",
                        extInterface.getCanonicalName(), params));
                // 扩展点方法的返回值不能是int/boolean等，否则会抛出NPE!
//                return null;
            }

            log.debug("use default {}", defaultExt);
            effectiveExts.add(new ExtensionMeta(defaultExt));
        }

        // all effective extension instances found
        List<R> accumulatedResults = new ArrayList<>(effectiveExts.size());
        R result = null;
        for (ExtensionMeta extensionMeta : effectiveExts) {
            result = invokeExtension(extensionMeta, method, args);
            accumulatedResults.add(result);

            if (reducer == null || reducer.shouldStop(accumulatedResults)) {
                break;
            }
        }

        if (reducer != null) {
            // reducer决定最终的返回值
            return this.reducer.reduce(accumulatedResults);
        }

        // 没有reducer，那么返回最后一个扩展点的执行结果
        return result;
    }

    private R invokeExtension(ExtensionMeta extensionMeta, final Method method, Object[] args) throws Throwable {
        try {
            return invokeExtensionMethod(extensionMeta, method, args);
        } catch (InvocationTargetException e) {
            // 此处接收被调用方法内部未被捕获的异常：扩展点里抛出异常
            log.error("{} code:{}", this.extInterface.getCanonicalName(), extensionMeta.getCode(), e.getTargetException());
            throw e.getTargetException();
        } catch (TimeoutException e) {
            log.error("timed out:{}ms, {} method:{} args:{}", timeoutInMs, extensionMeta.getExtensionBean(), method.getName(), args);
            // java里的TimeoutException继承Exception，需要转为ExtTimeoutException，否则上层看到的异常是 UndeclaredThrowableException
            throw new ExtTimeoutException(timeoutInMs);
        } catch (RejectedExecutionException e) {
            // TODO 需要加日志报警
            log.error("ExtInvokeTimer thread pool FULL:{}", e.getMessage());
            throw e;
        } catch (Throwable e) {
            // should never happen
            log.error("{} code:{} unexpected", this.extInterface.getCanonicalName(), extensionMeta.getCode(), e);
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    private R invokeExtensionMethod(ExtensionMeta extensionMeta, Method method, Object[] args) throws Throwable {
        IDomainExtension extInstance = extensionMeta.getExtensionBean();
        if (timeoutInMs > 0) {
            return invokeExtensionMethodWithTimeout(extInstance, method, args, timeoutInMs);
        }

        R result = (R) method.invoke(extInstance, args);
        log.debug("{} method:{} args:{}, result:{}", extInstance, method.getName(), args, result);

        return result;
    }

    @SuppressWarnings("unchecked")
    private R invokeExtensionMethodWithTimeout(IDomainExtension extInstance, Method method, Object[] args, final int timeoutInMs) throws Throwable {
        // 切换到线程池ThreadLocal会失效，目前ThreadLocal只有MDC
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        Future<R> future = EXT_INVOKE_TIMER_EXECUTOR.submit(() -> {
            MDC.setContextMap(mdcContext); // 手动继承前面线程的MDC
            try {
                return (R) method.invoke(extInstance, args);
            } finally {
                MDC.clear();
            }
        });

        try {
            R result = future.get(timeoutInMs, TimeUnit.MILLISECONDS);
            log.debug("{} method:{} args:{}, result:{}", extInstance, method.getName(), args, result);

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
