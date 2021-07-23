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

package org.lan.iti.cloud.jpa;

import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;
import org.lan.iti.cloud.util.WebUtils;
import org.lan.iti.cloud.web.page.PageParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.ServletRequestDataBinder;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * PageHelper for spring-data-jpa
 *
 * @author NorthLan
 * @date 2021-07-21
 * @url https://noahlan.com
 * @see PageRequest
 * @see Pageable
 */
@UtilityClass
public class JpaPageHelper {

    public static Pageable current() {
        return from(WebUtils.getCurrentRequest().orElse(null));
    }

    public static Pageable from(HttpServletRequest request) {
        PageParameter parameter = new PageParameter();
        if (request != null) {
            ServletRequestDataBinder binder = new ServletRequestDataBinder(parameter, "pageParameter");
            binder.bind(request);
        }
        return from(parameter);
    }

    public static Pageable from(PageParameter parameter) {
        return PageRequest.of(parameter.getPage(), parameter.getSize(), getSortByStrList(parameter.getSort()));
    }

    private static Sort getSortByStrList(List<String> sortList) {
        List<Sort.Order> orders = new ArrayList<>();
        sortList.forEach(it -> {
            if (!StrUtil.isEmpty(it)) {
                int separatorIdx = it.indexOf('#');
                if (separatorIdx >= 0) {
                    String property = it.substring(0, separatorIdx);
                    String direction = it.substring(separatorIdx + 1);
                    orders.add(new Sort.Order(Sort.Direction.fromString(direction), property));
                } else {
                    orders.add(Sort.Order.by(it));
                }
            }
        });
        if (orders.isEmpty()) {
            return Sort.unsorted();
        } else {
            return Sort.by(orders);
        }
    }
}
