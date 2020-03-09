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

import io.minio.ObjectStat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 存储对象的元数据
 *
 * @author NorthLan
 * @date 2020-03-04
 * @url https://noahlan.com
 */
@Data
@AllArgsConstructor
public class MinioObject implements Serializable {
    private static final long serialVersionUID = 1272364630912524942L;

    /**
     * 桶名
     */
    private String bucketName;

    /**
     * 文件名
     */
    private String name;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 长度
     */
    private Long length;

    /**
     * tag
     */
    private String etag;

    /**
     * content-type
     */
    private String contentType;

    /**
     * headers
     */
    private Map<String, List<String>> httpHeaders;

    public MinioObject(ObjectStat os) {
        this.bucketName = os.bucketName();
        this.name = os.name();
        this.createdTime = os.createdTime();
        this.length = os.length();
        this.etag = os.etag();
        this.contentType = os.contentType();
        this.httpHeaders = os.httpHeaders();
    }
}
