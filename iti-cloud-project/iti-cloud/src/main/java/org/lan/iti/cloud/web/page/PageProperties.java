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

package org.lan.iti.cloud.web.page;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 分页参数配置
 *
 * @author NorthLan
 * @date 2021-07-23
 * @url https://noahlan.com
 * @deprecated 弃用，使用SpringWebProperties调整
 */
@Data
@ConfigurationProperties(prefix = PageProperties.PREFIX)
@Deprecated
public class PageProperties {
    public final static String PREFIX = "iti.web";

    private Page page = new Page();

    private Sort sort = new Sort();

    @Data
    public static class Page {
        /**
         * Page index parameter name.
         */
        private String pageParameter = "page";

        /**
         * Page size parameter name.
         */
        private String sizeParameter = "size";

        /**
         * Whether to expose and assume 1-based page number indexes. Defaults to "false", meaning a page number of 0 in the request equals the first page.
         */
        private boolean oneIndexedParameters = false;

        /**
         * General prefix to be prepended to the page number and page size parameters.
         */
        private String prefix;

        /**
         * Default page size.
         */
        private int defaultPageSize = 20;

        /**
         * Maximum page size to be accepted.
         */
        private int maxPageSize = 2000;
    }

    @Data
    public static final class Sort {
        /**
         * Sort parameter name.
         */
        private String sortParameter = "sort";
    }
}
