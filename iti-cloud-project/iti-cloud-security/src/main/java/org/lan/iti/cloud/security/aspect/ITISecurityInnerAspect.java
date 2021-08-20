///*
// *
// *  * Copyright (c) [2019-2021] [NorthLan](lan6995@gmail.com)
// *  *
// *  * Licensed under the Apache License, Version 2.0 (the "License");
// *  * you may not use this file except in compliance with the License.
// *  * You may obtain a copy of the License at
// *  *
// *  *     http://www.apache.org/licenses/LICENSE-2.0
// *  *
// *  * Unless required by applicable law or agreed to in writing, software
// *  * distributed under the License is distributed on an "AS IS" BASIS,
// *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  * See the License for the specific language governing permissions and
// *  * limitations under the License.
// *
// */
//
//package org.lan.iti.cloud.security.aspect;
//
//import cn.hutool.core.util.StrUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.lan.iti.cloud.security.annotation.Inner;
//import org.lan.iti.common.core.constants.SecurityConstants;
//import org.springframework.core.annotation.AnnotationUtils;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.core.SpringSecurityMessageSource;
//import org.springframework.web.context.request.RequestAttributes;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//
///**
// * 服务间接口不鉴权处理逻辑
// * 基于 AOP
// *
// * @author NorthLan
// * @date 2020-02-24
// * @url https://noahlan.com
// */
//@Slf4j
//@Aspect
//public class ITISecurityInnerAspect {
//
//    @Around("@within(inner) || @annotation(inner)")
//    public Object around(ProceedingJoinPoint point, Inner inner) throws Throwable {
//        HttpServletRequest request = null;
//        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
//        if (attributes instanceof ServletRequestAttributes) {
//            request = ((ServletRequestAttributes) attributes).getRequest();
//        }
//        if (request == null) {
//            return point.proceed();
//        }
//        // inner为空则获取类上的注解
//        if (inner == null) {
//            Class<?> targetClazz = point.getTarget().getClass();
//            inner = AnnotationUtils.findAnnotation(targetClazz, Inner.class);
//        }
//        if (inner == null) {
//            return point.proceed();
//        }
//
//        String header = request.getHeader(SecurityConstants.FROM);
//        if (inner.value() && !StrUtil.equals(SecurityConstants.FROM_IN, header)) {
//            log.warn("访问接口 {} 没有权限", point.getSignature().getName());
//            throw new AccessDeniedException(SpringSecurityMessageSource.getAccessor().getMessage(
//                    "AbstractAccessDecisionManager.accessDenied", new Object[]{inner.value()}, "access denied"));
//        }
//        return point.proceed();
//    }
//}
