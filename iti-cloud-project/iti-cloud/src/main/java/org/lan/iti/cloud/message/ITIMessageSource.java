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

package org.lan.iti.cloud.message;

import org.lan.iti.common.core.constants.CommonConstants;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * 框架定义 MessageSource
 *
 * @author NorthLan
 * @date 2020-04-10
 * @url https://noahlan.com
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class ITIMessageSource extends ReloadableResourceBundleMessageSource {

    public ITIMessageSource() {
        setBasename(CommonConstants.DEFAULT_MESSAGES);
    }

    public static MessageSourceAccessor getAccessor() {
        return new MessageSourceAccessor(new ITIMessageSource());
    }
}
