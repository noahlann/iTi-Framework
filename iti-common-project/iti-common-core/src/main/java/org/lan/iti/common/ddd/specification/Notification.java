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

package org.lan.iti.common.ddd.specification;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 业务规约收集、通知器
 *
 * @author NorthLan
 * @date 2021-02-22
 * @url https://noahlan.com
 */
public class Notification {

    /**
     * 不满足业务规约的原因列表
     */
    @Getter
    private final List<String> reasons;

    /**
     * 创建空的 {@code Notification}
     */
    public static Notification create() {
        return new Notification();
    }

    private Notification() {
        this.reasons = new ArrayList<>();
    }

    /**
     * 添加违反业务规约的原因
     *
     * @param reason 原因描述
     * @return 如果原因正常添加，则返回true
     */
    public boolean addReason(String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            return false;
        }

        // 重复检查
        for (String s : reasons) {
            if (s.equals(reason)) {
                return false;
            }
        }
        return this.reasons.add(reason);
    }

    /**
     * 违反业务规约原因是否为空
     */
    public boolean isEmpty() {
        return this.reasons.isEmpty();
    }

    /**
     * 原因总数
     */
    public int size() {
        return this.reasons.size();
    }

    /**
     * 获取第一个原因描述
     */
    public String firstReason() {
        if (isEmpty()) {
            return null;
        }
        return reasons.get(0);
    }
}
