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

package org.lan.iti.common.core.sensitive.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

/**
 * 脱敏序列化工具类
 *
 * @author NorthLan
 * @date 2020-02-22
 * @url https://noahlan.com
 */
@UtilityClass
public class DesensitizedUtils {

    /**
     * 对字符串进行脱敏操作
     *
     * @param origin          原始字符串
     * @param prefixNoMaskLen 左侧需要保留几位明文字段
     * @param suffixNoMaskLen 右侧需要保留几位明文字段
     * @param maskStr         用于遮罩的字符串, 如'*'
     * @return 脱敏后结果
     */
    public String desValue(String origin, int prefixNoMaskLen, int suffixNoMaskLen, String maskStr) {
        if (StringUtils.isEmpty(origin)) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        final int n = origin.length();
        int i = 0;
        while (i < n) {
            if (i < prefixNoMaskLen) {
                sb.append(origin.charAt(i));
                ++i;
                continue;
            }
            if (i > n - suffixNoMaskLen - 1) {
                sb.append(origin.charAt(i));
                ++i;
                continue;
            }
            sb.append(maskStr);
            ++i;
        }
        return sb.toString();
    }

    /**
     * 【中文姓名】只显示最后一个汉字，其他隐藏为星号，比如：**梦
     *
     * @param fullName 姓名
     * @return 结果
     */
    public String chineseName(String fullName) {
        return desValue(fullName, 0, 1, "*");
    }

    /**
     * 【身份证号】显示前六位, 四位，其他隐藏。共计18位或者15位，比如：340304*******1234
     *
     * @param id 身份证号码
     * @return 结果
     */
    public String idCardNum(String id) {
        return desValue(id, 6, 4, "*");
    }

    /**
     * 【固定电话】后四位，其他隐藏，比如 ****1234
     *
     * @param num 固定电话
     * @return 结果
     */
    public String fixedPhone(String num) {
        return desValue(num, 0, 4, "*");
    }

    /**
     * 【手机号码】前三位，后四位，其他隐藏，比如135****6810
     *
     * @param num 手机号码
     * @return 结果
     */
    public String mobilePhone(String num) {
        return desValue(num, 3, 4, "*");
    }

    /**
     * 【地址】只显示到地区，不显示详细地址，比如：北京市海淀区****
     *
     * @param address 地址
     * @return 结果
     */
    public String address(String address) {
        return desValue(address, 6, 0, "*");
    }

    /**
     * 【电子邮箱 邮箱前缀仅显示第一个字母，前缀其他隐藏，用星号代替，@及后面的地址显示，比如：d**@126.com
     *
     * @param email 电子邮箱
     * @return 结果
     */
    public String email(String email) {
        if (StringUtils.isEmpty(email)) {
            return "";
        }
        int index = email.indexOf("@");
        if (index <= 1) {
            return email;
        }
        String preEmail = desValue(email.substring(0, index), 1, 0, "*");
        return preEmail + email.substring(index);
    }

    /**
     * 【银行卡号】前六位，后四位，其他用星号隐藏每位1个星号，比如：622260**********1234
     *
     * @param cardNum 银行卡号
     * @return 结果
     */
    public String bankCard(String cardNum) {
        return desValue(cardNum, 6, 4, "*");
    }

    /**
     * 【密码】密码的全部字符都用*代替，比如：******
     *
     * @param password 密码
     * @return 结果
     */
    public String password(String password) {
        return StringUtils.isEmpty(password) ? "" : "******";
    }

    /**
     * 【密钥】密钥除了最后三位，全部都用*代替，比如：***xdS
     * 脱敏后长度为6，如果明文长度不足三位，则按实际长度显示，剩余位置补*
     *
     * @param key 密钥
     * @return 结果
     */
    public String key(String key) {
        if (StringUtils.isEmpty(key)) {
            return "";
        }
        final int viewLength = 6;
        final StringBuilder tmpKey = new StringBuilder(desValue(key, 0, 3, "*"));
        String result;
        if (tmpKey.length() > viewLength) {
            result = tmpKey.substring(tmpKey.length() - viewLength);
        } else if (tmpKey.length() < viewLength) {
            int buffLength = viewLength - tmpKey.length();
            for (int i = 0; i < buffLength; ++i) {
                tmpKey.insert(0, "*");
            }
            result = tmpKey.toString();
        } else {
            result = tmpKey.toString();
        }
        return result;
    }
}
