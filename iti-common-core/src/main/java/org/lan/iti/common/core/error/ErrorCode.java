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

package org.lan.iti.common.core.error;

import lombok.Builder;
import lombok.Getter;
import org.lan.iti.common.core.constants.ITIConstants;
import org.springframework.util.Assert;

/**
 * 异常编码
 * <pre>
 *     位置	1	2	3	4	5	6	7	8	9	10
 *      说明	规范版本	固定标识	错误类型	错误级别	错误编码
 *      示例	1	0	0	1	2	2	0	0	0	1
 * </pre>
 *
 * @author NorthLan
 * @date 2020-05-15
 * @url https://noahlan.com
 */
@SuppressWarnings("FieldMayBeFinal")
@Builder
@Getter
public class ErrorCode {
    /**
     * 版本号
     */
    @Builder.Default
    private int version = ITIConstants.EXCEPTION_ERROR_CODE_VERSION;

    /**
     * 固定标识
     */
    private int mark;

    /**
     * 错误类型
     */
    private int type;

    /**
     * 错误级别
     */
    private int level;

    /**
     * 错误代码
     */
    private int code;

    @Override
    public String toString() {
        Assert.isTrue(version >= 0, "异常码版本号不能为负数");
        Assert.isTrue(mark >= 0, "固定标识代码不能为负数");
        Assert.isTrue(type >= 0, "错误类型代码不能为负数");
        Assert.isTrue(level >= 0, "错误级别代码不能为负数");
        Assert.isTrue(code >= 0, "错误编码不能为负数");
        Assert.isTrue(version <= 9, "异常码版本号不能超过1位数字");
        Assert.isTrue(mark <= 999, "固定标识代码不能超过3位数字");
        Assert.isTrue(type <= 9, "错误类型代码不能超过1位数字");
        Assert.isTrue(level <= 9, "错误级别代码不能超过1位数字");
        Assert.isTrue(code <= 9999, "错误编码不能超过4位数字");

        return version +
                String.format("%03d", mark) +
                String.format("%01d", type) +
                String.format("%01d", level) +
                String.format("%04d", code);
    }
}
