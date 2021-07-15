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

package org.lan.iti.cloud.iha.server.provider;

import cn.hutool.core.util.ObjectUtil;
import org.lan.iti.cloud.iha.server.model.Scope;
import org.lan.iti.cloud.iha.server.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
public class ScopeProvider {
    private static final List<Scope> SCOPES = new ArrayList<>();

    static {
        addScope(new Scope("read", "Allow users to read protected resources."));
        addScope(new Scope("write", "Allow users to operate (add, delete, modify) protected resources."));
        addScope(new Scope("openid", "OpenID connect must include scope."));
        addScope(new Scope("profile", "Allow access to user's basic information."));
        addScope(new Scope("email", "Allow access to user's mailbox."));
        addScope(new Scope("phone", "Allow access to the user’s phone number."));
        addScope(new Scope("address", "Allow access to the user's address."));
        addScope(new Scope("roles", "Allow access to the user's roles."));
    }

    /**
     * Add a single scope.
     * Note: This method is to add data to the existing scope collection.
     * <p>
     * If the scope to be added already exists, the new scope will overwrite the original scope
     *
     * @param scope single scope
     */
    public static void addScope(Scope scope) {
        if (null == scope || StringUtil.isEmpty(scope.getCode())) {
            return;
        }
        long num = SCOPES.stream().filter(item -> item.getCode().equals(scope.getCode())).count();
        if (num > 0) {
            SCOPES.stream()
                    .filter(item -> item.getCode().equals(scope.getCode()))
                    .findFirst()
                    .map(item -> item.setDescription(scope.getDescription()));
            return;
        }
        SCOPES.add(scope);
    }

    /**
     * Return the set of available scopes after deduplication according to the scope code
     *
     * @return Unique scope collection
     */
    public static List<Scope> getScopes() {
        return SCOPES.stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(
                        Comparator.comparing(
                                Scope::getCode))), ArrayList::new));
    }

    /**
     * Obtain the scope collection through scope code (does not contain duplicates)
     *
     * @param codes scope code
     * @return Unique scope collection
     */
    public static List<Scope> getScopeByCodes(Collection<String> codes) {
        if (ObjectUtil.isEmpty(codes)) {
            return new ArrayList<>(0);
        }
        return Optional.ofNullable(SCOPES.stream().filter((scope) -> codes.contains(scope.getCode()))
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(
                                Comparator.comparing(
                                        Scope::getCode))), ArrayList::new)))
                .orElse(new ArrayList<>(0));
    }

    /**
     * Get the code of all scopes
     *
     * @return code of all scopes
     */
    public static List<String> getScopeCodes() {
        return SCOPES.stream().map(Scope::getCode).distinct().collect(Collectors.toList());
    }
}
