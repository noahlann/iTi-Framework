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

package org.lan.iti.cloud.ddd.aop;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ArrayUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.lan.iti.cloud.ddd.annotation.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Logger切面
 *
 * @author NorthLan
 * @date 2021-03-02
 * @url https://noahlan.com
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AutoLoggerStepAspect {

    @Around("execution(* *..IDomainStep.execute(..))")
    public Object stepAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object stepInterface = joinPoint.getTarget();

        // 获取该类实际的log对象,避免log展示包名为本类
        Logger log = LoggerFactory.getLogger(stepInterface.getClass());

        Step step = AnnotationUtil.getAnnotation(stepInterface.getClass(), Step.class);
        if (step == null) {
            return joinPoint.proceed();
        }
        String paramStr = Arrays.stream(joinPoint.getArgs())
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.joining(" | "));

        // descSb
        String descSb = "【" + step.name() + "】" + " ";
        if (!ArrayUtil.isEmpty(step.tags())) {
            descSb += "(" + ArrayUtil.join(step.tags(), ",") + ")" + " ";
        }
        log.debug(descSb + "执行开始");
        log.debug("参数：" + paramStr);

        Object result = joinPoint.proceed();

        log.debug(descSb + "执行完毕");
        return result;
    }
}
