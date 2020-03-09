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

package org.lan.iti.common.minio.vo;

import io.minio.messages.Item;
import io.minio.messages.Owner;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * minio 桶中的对象信息
 *
 * @author NorthLan
 * @date 2020-03-04
 * @url https://noahlan.com
 */
@Data
@AllArgsConstructor
public class MinioItem implements Serializable {
    private static final long serialVersionUID = 5458654995167295881L;

    /**
     * 对象名
     */
    private String objectName;

    /**
     * 最后修改时间
     */
    private Date lastModified;

    /**
     * tag
     */
    private String etag;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 存储class
     */
    private String storageClass;

    /**
     * 拥有者
     */
    private Owner owner;

    /**
     * 类型
     */
    private String type;

    public MinioItem(Item item) {
        this.objectName = item.objectName();
        this.lastModified = item.lastModified();
        this.etag = item.etag();
        this.size = (long) item.size();
        this.storageClass = item.storageClass();
        this.owner = item.owner();
        this.type = item.isDir() ? "directory" : "file";
    }
}
