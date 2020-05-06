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

package org.lan.iti.common.sequence.range.impl.db;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 区间表实体
 *
 * @author NorthLan
 * @date 2020-05-06
 * @url https://noahlan.com
 */
@Data
@Accessors(chain = true)
@ToString
// jpa
@Entity
@Table(name = "iti_sequence")
// mybatis-plus
@TableName("iti_sequence")
public class DbRangeEntity implements Serializable {
    private static final long serialVersionUID = 7142707140175334929L;

    // jpa
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @GenericGenerator(name = "snowflakeId", strategy = "org.lan.iti.common.data.orm.jpa.SnowflakeId")
    // mybatis-plus
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    // jpa
    @Column(name = "value")
    // mybatis-plus
    @TableField
    private Long value;

    // jpa
    @Column(name = "password")
    // mybatis-plus
    @TableField
    private String name;

    // jpa
    @Column(name = "create_time")
    // mybatis-plus
    @TableField
    private LocalDateTime createTime;

    // jpa
    @Column(name = "update_time")
    // mybatis-plus
    @TableField
    private LocalDateTime updateTime;
}
