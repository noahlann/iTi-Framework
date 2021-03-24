/*
 * Copyright (c) [2019-2021] [NorthLan](lan6995@gmail.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.cloud.mock.domain.aggregate.user;

import lombok.*;
import org.lan.iti.cloud.mock.domain.aggregate.user.vo.LoginInfo;
import org.lan.iti.cloud.mock.domain.aggregate.user.vo.UserStatusEnum;
import org.lan.iti.cloud.mock.spec.enums.SexEnum;
import org.lan.iti.common.ddd.model.IDomain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户聚合
 *
 * @author NorthLan
 * @date 2021-02-03
 * @url https://noahlan.com
 */
@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User implements IDomain {

    /**
     * 唯一标识
     */
    @Setter
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 注册者IPv4地址
     */
    private String registerIpv4;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 真实姓名
     */
    private String realname;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 出生日期
     */
    private LocalDateTime birthday;

    // region ValueObject
    /**
     * 登录信息
     */
    private LoginInfo loginInfo;

    /**
     * 用户状态
     */
    private UserStatusEnum status;

    /**
     * 性别
     */
    private SexEnum sex = SexEnum.SECURE;
    // endregion

    // region Relation
    /**
     * 租户ID
     * 聚合外使用ID关联
     */
    private String tenantId;

    /**
     * 角色ID列表
     * 聚合外使用ID关联
     */
    private Set<String> roles = new HashSet<>();

    /**
     * 部门ID列表
     * 聚合外使用ID关联
     */
    private Set<String> depts = new HashSet<>();

    /**
     * 分组ID列表
     * 聚合外使用ID关联
     */
    private Set<String> groups = new HashSet<>();
    // endregion
}
