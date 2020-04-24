/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.common.core.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 框架通用 Model
 * <p>
 * createTime createBy 的 INSERT_FILL
 * updateTime updateBy 的 UPDATE_FILL
 *
 * @author NorthLan
 * @date 2020/02/20
 * @url https://noahlan.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class BaseModel<T extends BaseModel<T>> extends Model<T> {
    private static final long serialVersionUID = -8727073493362896056L;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty("创建时间")
    protected LocalDateTime createTime;

    /**
     * 创建人 ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty("创建者ID")
    protected Long createBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty("更新时间")
    protected LocalDateTime updateTime;

    /**
     * 更新人 ID
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty("更新者ID")
    protected Long updateBy;
}
