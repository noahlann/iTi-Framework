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

package org.lan.iti.cloud.security.component;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.cloud.security.annotation.Inner;
import org.lan.iti.cloud.security.properties.SecurityProperties;
import org.lan.iti.common.core.util.StringPool;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 服务器资源暴露处理器
 *
 * @author NorthLan
 * @date 2020-02-24
 * @url https://noahlan.com
 */
@Slf4j
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "security.oauth2.client")
public class PermitAllUrlResolver implements InitializingBean {
    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();
    private static final Pattern PATTERN = Pattern.compile("\\{(.*?)}");

    private final WebApplicationContext applicationContext;
    private final SecurityProperties properties;

    @Getter
    @Setter
    private List<String> ignoreUrls = new ArrayList<>();

    @Override
    public void afterPropertiesSet() {
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

        for (RequestMappingInfo info : map.keySet()) {
            HandlerMethod handlerMethod = map.get(info);
            PatternsRequestCondition requestCondition = info.getPatternsCondition();
            if (requestCondition == null) {
                continue;
            }

            // 1. 获取类上边的注解
            Inner controller = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), Inner.class);
            Optional.ofNullable(controller)
                    .ifPresent(inner -> requestCondition.getPatterns()
                            .forEach(url -> ignoreUrls.add(ReUtil.replaceAll(url, PATTERN, StringPool.ASTERISK))));

            // 2. 当类上不包含 @Inner 时，获取方法注解
            if (controller == null) {
                // 获取方法上边的注解 替代path variable 为 *
                Inner method = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), Inner.class);
                Optional.ofNullable(method)
                        .ifPresent(inner -> requestCondition.getPatterns()
                                .forEach(url -> this.filterPath(url, info, map)));
                continue;
            }

            // 3. 当类上包含@Inner注解 判断handlerMethod是否包含在Inner类中
            Class<?> beanType = handlerMethod.getBeanType();
            Method[] methods = beanType.getDeclaredMethods();
            Method method = handlerMethod.getMethod();
            if (ArrayUtil.contains(methods, method)) {
                requestCondition.getPatterns().forEach(url -> this.filterPath(url, info, map));
            }

        }
    }

    /**
     * 过滤 Inner 设置
     * <p>
     * 0. 暴露安全检查 1. 路径转换： 如果为restful(/xx/{xx}) --> /xx/* ant 表达式 2.
     * 构建表达式：允许暴露的接口|允许暴露的方法类型,允许暴露的方法类型 URL|GET,POST,DELETE,PUT
     * </p>
     *
     * @param url  mapping路径
     * @param info 请求犯法
     * @param map  路由映射信息
     */
    private void filterPath(String url, RequestMappingInfo info, Map<RequestMappingInfo, HandlerMethod> map) {
        // 安全检查
        if (properties.isBootInnerCheck()) {
            security(url, info, map);
        }

        List<String> methodList = info.getMethodsCondition().getMethods().stream().map(RequestMethod::name)
                .collect(Collectors.toList());
        String resultUrl = ReUtil.replaceAll(url, PATTERN, "*");
        if (CollUtil.isEmpty(methodList)) {
            ignoreUrls.add(resultUrl);
        } else {
            ignoreUrls.add(String.format("%s|%s", resultUrl, CollUtil.join(methodList, StrUtil.COMMA)));
        }
    }

    /**
     * 针对 PathVariable 请求进行安全检查。
     * <p>
     * 增加启动耗时影响启动效率 请注意
     *
     * @param url 接口路径
     * @param rq  当前请求的元信息
     * @param map springmvc 接口列表
     */
    @SuppressWarnings("ConstantConditions")
    private void security(String url, RequestMappingInfo rq, Map<RequestMappingInfo, HandlerMethod> map) {
        // 判断 URL 是否是 rest path 形式
        if (!StrUtil.containsAny(url, StrUtil.DELIM_START, StrUtil.DELIM_END)) {
            return;
        }

        for (RequestMappingInfo info : map.keySet()) {
            Set<RequestMethod> methods = info.getMethodsCondition().getMethods();
            // 如果请求方法不匹配跳过
            if (!CollUtil.containsAny(methods, rq.getMethodsCondition().getMethods())) {
                continue;
            }

            // 如果请求方法路径匹配
            Set<String> patterns = info.getPatternsCondition().getPatterns();
            for (String pattern : patterns) {
                // 跳过自身
                if (StrUtil.equals(url, pattern)) {
                    continue;
                }

                if (PATH_MATCHER.match(url, pattern)) {
                    HandlerMethod rqMethod = map.get(rq);
                    HandlerMethod infoMethod = map.get(info);
                    log.error("@Inner 标记接口 ==> {}.{} 使用不当，会额外暴露接口 ==> {}.{} 请知悉", rqMethod.getBeanType().getName(),
                            rqMethod.getMethod().getName(), infoMethod.getBeanType().getName(),
                            infoMethod.getMethod().getName());
                }
            }
        }
    }

    /**
     * 获取对外暴露的URL，注册到 spring security
     *
     * @param registry spring security context
     */
    public void registry(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        for (String url : getIgnoreUrls()) {
            String[] strings = StrUtil.split(url, "|");

            // 仅配置对外暴露的URL ，则注册到 spring security的为全部方法
            if (strings.length == 1) {
                registry.antMatchers(strings[0]).permitAll();
                continue;
            }

            // 当配置对外的URL|GET,POST 这种形式，则获取方法列表 并注册到 spring security
            if (strings.length == 2) {
                for (String method : StrUtil.split(strings[1], StrUtil.COMMA)) {
                    registry.antMatchers(HttpMethod.valueOf(method), strings[0]).permitAll();
                }
                continue;
            }

            log.warn("{} 配置无效，无法配置对外暴露", url);
        }
    }
}
