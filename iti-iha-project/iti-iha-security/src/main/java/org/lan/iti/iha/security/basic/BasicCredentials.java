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

package org.lan.iti.iha.security.basic;

import cn.hutool.core.codec.Base64;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Credentials in Basic authentication.
 *
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 * @see <a href="http://tools.ietf.org/html/rfc2617#section-2">RFC 2617 (HTTP Authentication), 2. Basic Authentication Scheme</a>
 */
@Getter
public class BasicCredentials {
    /**
     * Regular expression to parse {@code Authorization} header.
     */
    private static final Pattern CHALLENGE_PATTERN = Pattern.compile("^Basic *([^ ]+) *$", Pattern.CASE_INSENSITIVE);

    private final String id;
    private final String secret;


    /**
     * "Basic {base64-encoded id:secret}"
     */
    private transient String formatted;

    private BasicCredentials() {
        this(null, null);
    }

    /**
     * Constructor with credentials.
     *
     * @param id     The client ID.
     * @param secret The client secret.
     */
    private BasicCredentials(String id, String secret) {
        this.id = null == id ? "" : id;
        this.secret = null == secret ? "" : secret;
    }

    /**
     * Parse {@code Authorization} header for Basic authentication.
     *
     * @param input The value of {@code Authorization} header. Expected inputs
     *              are either <code>"Basic <i>{Base64-Encoded-id-and-secret}</i>"</code>,
     *              or <code>"<i>{Base64-Encoded-id-and-secret}</i>"</code>.
     * @return Parsed credentials. If {@code input} is {@code null} is returned.
     */
    public static BasicCredentials parse(String input) {
        if (input == null) {
            return new BasicCredentials();
        }

        Matcher matcher = CHALLENGE_PATTERN.matcher(input);
        if (!matcher.matches()) {
            return new BasicCredentials();
        }

        String encoded = matcher.group(1);
        byte[] decoded = Base64.decode(encoded);
        String value = new String(decoded, StandardCharsets.UTF_8);
        String[] credentials = value.split(":", 2);

        String id = credentials[0];
        String secret = null;
        if (credentials.length == 2) {
            secret = credentials[1];
        }
        return new BasicCredentials(id, secret);
    }

    /**
     * Create a value suitable as the value of {@code Authorization} header.
     *
     * @return {@code Authorization} header value for Basic authentication.
     */
    public String create() {
        if (formatted != null) {
            return formatted;
        }

        String credentials = String.format("%s:%s", id, secret);
        byte[] credentialsBytes = credentials.getBytes(StandardCharsets.UTF_8);
        String encoded = Base64.encode(credentialsBytes);

        return (formatted = "Basic " + encoded);
    }
}
