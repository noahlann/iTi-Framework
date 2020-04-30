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

package org.lan.iti.common.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;
import java.io.IOException;

/**
 * iTi 文件上传配置
 *
 * @author NorthLan
 * @date 2020-04-30
 * @url https://noahlan.com
 */
@Data
@ConfigurationProperties(prefix = "iti.multipart")
public class MultipartProperties {
    /**
     * Whether to enable iTi support of multipart uploads.
     */
    private boolean enabled = true;

    /**
     * Intermediate location of uploaded files.
     */
    private Resource uploadTempDir;

    /**
     * Default encoding
     */
    private String defaultEncoding = "UTF-8";

    /**
     * Max upload size.
     */
    private DataSize maxUploadSize = DataSize.ofBytes(-1);

    /**
     * Max upload size per file.
     */
    private DataSize maxUploadSizePerFile = DataSize.ofBytes(-1);

    /**
     * Max in-memory size.
     */
    private DataSize maxInMemorySize = DataSize.ofBytes(10240);

    /**
     * Whether to resolve the multipart request lazily at the time of file or parameter
     * access.
     */
    private boolean resolveLazily = false;

    /**
     * Create a new {@link MultipartConfigElement} using the properties.
     *
     * @return a new {@link MultipartConfigElement} configured using there properties
     */
    public MultipartConfigElement createMultipartConfig() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from(this.maxInMemorySize).to(factory::setFileSizeThreshold);
        map.from(this.maxUploadSize).to(factory::setMaxRequestSize);
        map.from(this.maxUploadSizePerFile).to(factory::setMaxFileSize);
        map.from(this.getUploadTempDir()).when(it -> {
            boolean result = it.exists();
            try {
                if (!result) {
                    result = it.getFile().mkdir();
                }
            } catch (IOException e) {
                // just catch it
            }
            return result;
        }).to(it -> {
            try {
                factory.setLocation(it.getFile().getPath());
            } catch (IOException e) {
                // just catch it
            }
        });
        return factory.createMultipartConfig();
    }
}
