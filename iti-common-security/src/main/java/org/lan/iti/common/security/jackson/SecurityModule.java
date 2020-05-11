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

package org.lan.iti.common.security.jackson;

import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.lan.iti.common.security.model.SecurityUser;

/**
 * SecurityUser对应的Jackson-Module
 *
 * @author NorthLan
 * @date 2020-05-11
 * @url https://noahlan.com
 */
public class SecurityModule extends SimpleModule {
    private static final long serialVersionUID = -2750906611250100058L;

    public SecurityModule() {
        super(PackageVersion.VERSION);
        this.setMixInAnnotation(SecurityUser.class, SecurityUserMixin.class);
    }
}
