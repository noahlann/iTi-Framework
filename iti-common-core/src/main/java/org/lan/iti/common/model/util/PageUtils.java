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

package org.lan.iti.common.model.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.experimental.UtilityClass;
import org.lan.iti.common.core.util.WebUtils;
import org.lan.iti.common.model.page.PageParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.ServletRequestDataBinder;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 分页工具类
 *
 * @author NorthLan
 * @date 2020-04-10
 * @url https://noahlan.com
 * @deprecated 将使用jpa的分页器直接替代 {@link org.springframework.data.domain.PageRequest}
 */
@UtilityClass
@Deprecated
public class PageUtils {

    /**
     * 构建Mybatis-plus分页参数
     * <p>
     * 默认取当前Request中与PageParameter相对应的参数
     * </p>
     *
     * @param <T> 实体泛型
     */
    public <T> IPage<T> build() {
        HttpServletRequest request = WebUtils.getCurrentRequest().orElse(null);
        PageParameter parameter = new PageParameter();
        if (request != null) {
            ServletRequestDataBinder binder = new ServletRequestDataBinder(parameter, "pageParameter");
            binder.bind(request);
        }
        return build(parameter);
    }

    /**
     * 构建Mybatis-plus分页参数
     * <p>
     * 通过给定的 PageParameter 构建
     * </p>
     *
     * @param parameter 给定的 PageParameter
     * @param <T>       实体泛型
     */
    public <T> IPage<T> build(@NonNull PageParameter parameter) {
        return new Page<T>(parameter.getCurrent(), parameter.getSize()).addOrder(parameter.getOrders());
    }

    /**
     * Page对象转换
     *
     * @param source      源page
     * @param convertFunc 单对象转换方法
     * @param <T>         源对象类型
     * @param <R>         目标对象类型
     */
    public <T, R> IPage<R> convert(IPage<T> source, Function<T, R> convertFunc) {
        Page<R> result = new Page<>(source.getCurrent(), source.getSize(), source.getTotal(), source.isSearchCount());
        List<R> resultRecords = new ArrayList<>(source.getRecords().size());
        // 转换
        source.getRecords().forEach(it -> resultRecords.add(convertFunc.apply(it)));
        // 设定值
        result.setRecords(resultRecords);
        return result;
    }
}
