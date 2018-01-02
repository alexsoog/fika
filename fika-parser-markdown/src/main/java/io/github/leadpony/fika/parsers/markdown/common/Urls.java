/*
 * Copyright 2017 the original author or authors.
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
package io.github.leadpony.fika.parsers.markdown.common;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;

/**
 * @author leadpony
 *
 */
public final class Urls {
    
    private static final String RESERVED_CHARS =
            "!*'();:@&=+$,/?#[]" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.~";
    private static final BitSet reservedCharSet = new BitSet();
    
    static {
        for (char c: RESERVED_CHARS.toCharArray()) {
            reservedCharSet.set(c);
        }
    }

    public static String encode(String url) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < url.length(); ++i) {
            char c = url.charAt(i);
            if (reservedCharSet.get(c)) {
                sb.append(c);
            } else {
                String str = String.valueOf(c);
                byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
                for (byte b: bytes) {
                    sb.append("%");
                    sb.append(Integer.toHexString(b).toUpperCase());
                }
            }
        }
        return sb.toString();
    }
    
    private Urls() {
    }
}
