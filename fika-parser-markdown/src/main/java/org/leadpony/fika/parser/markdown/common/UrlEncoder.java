/*
 * Copyright 2017-2019 the Fika authors.
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
package org.leadpony.fika.parser.markdown.common;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author leadpony
 */
public class UrlEncoder {
    
    private static final String URI = 
            "((?<scheme>[^:]*):)?" +
            "(?<schemaSpecificPart>[^#]*)" +
            "(#(?<fragment>.*))?";

    private static final String USERINFO = "(?<userinfo>[^@]*@)";
    private static final String HOST = "(?<host>[^/]*)";
            
    private static final String PATH = "(?<path>[^?#]*)";
    private static final String QUERY = "\\?(?<query>[^#]*)";
    private static final String RELATIVE_URI = PATH + "(" + QUERY + ")?";
    private static final String HIERARCHICAL_URI =
            "(?<authority>//" +
            "(" + USERINFO + ")?" +
            HOST +         
            ")?" +
            PATH +
            "(" + QUERY + ")?"
            ;
    
    private static final Pattern URI_PATTERN = Pattern.compile(URI);
    private static final Pattern HIERARCHICAL_URL_PATTERN = Pattern.compile(HIERARCHICAL_URI);
    private static final Pattern RELATIVE_URI_PATTERN = Pattern.compile(RELATIVE_URI);
  
    private static final String ALLOWED_CHARS =
            "!*'();:@&=+$,/?#[]" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.~";
    
    @SuppressWarnings("serial")
    private static final BitSet ALLOWED_CHAR_SET = new BitSet() {{
        for (char c: ALLOWED_CHARS.toCharArray()) {
            set(c);
        }
        set('%');
    }};

    private final static char[] HEXCHARS = "0123456789ABCDEF".toCharArray();
    
    private final StringBuilder builder = new StringBuilder();
    
    public String encode(String url) {
        Matcher m = URI_PATTERN.matcher(url);
        if (m.matches()) {
            if (m.group("scheme") != null) {
                appendScheme(m.group("scheme"));
                appendSchemeSpecificPart(m.group("schemaSpecificPart"));
            } else {
                appendRelativeUri(m.group("schemaSpecificPart"));
            }
            if (m.group("fragment") != null) {
                appendFragment(m.group("fragment"));
            }
        } else {
            return url;
        }
        return builder.toString();
    }
    
    private void appendScheme(String scheme) {
        builder.append(scheme).append(":");
    }
    
    private void appendSchemeSpecificPart(String schemeSpecificPart) {
        if (schemeSpecificPart.isEmpty()) {
            return;
        }
        if (schemeSpecificPart.charAt(0) == '/') {
            appendHierarchicalUri(schemeSpecificPart);
        } else {
            appendOpaque(schemeSpecificPart);
        }
    }
    
    private void appendOpaque(String uri) {
        builder.append(uri);
    }
    
    private void appendHierarchicalUri(String uri) {
        Matcher m = HIERARCHICAL_URL_PATTERN.matcher(uri);
        if (m.matches()) {
            if (m.group("authority") != null) {
                builder.append("//");
                if (m.group("userinfo") != null) {
                    appendUserinfo(m.group("userinfo"));
                }
                appendHost(m.group("host"));
            }
            appendPath(m.group("path"));
            if (m.group("query") != null) {
                appendQuery(m.group("query"));
            }
        }
    }
    
    private void appendRelativeUri(String uri) {
        Matcher m = RELATIVE_URI_PATTERN.matcher(uri);
        if (m.matches()) {
            appendPath(m.group("path"));
            if (m.group("query") != null) {
                appendQuery(m.group("query"));
            }
        }
    }
   
    private void appendUserinfo(String userinfo) {
        for (int i = 0; i < userinfo.length(); ++i) {
            char c = userinfo.charAt(i);
            if (!inAllowedSet(c) || isUserinfoPercentEncodeSet(c)) {
                appendPercentEncoded(c);
            } else {
                builder.append(c);
            }
        }
    }

    private void appendHost(String host) {
        for (int i = 0; i < host.length(); ++i) {
            char c = host.charAt(i);
            if (!inAllowedSet(c)) {
                appendPercentEncoded(c);
            } else {
                builder.append(c);
            }
        }
    }

    private void appendPath(String path) {
        for (int i = 0; i < path.length(); ++i) {
            char c = path.charAt(i);
            if (!inAllowedSet(c) || inPathPercentEncodeSet(c)) {
                appendPercentEncoded(c);
            } else {
                builder.append(c);
            }
        }
    }
   
    private void appendQuery(String query) {
        builder.append('?');
        for (int i = 0; i < query.length(); ++i) {
            char c = query.charAt(i);
            if (!inAllowedSet(c) || inQueryPercentEncodeSet(c)) {
                appendPercentEncoded(c);
            } else {
                builder.append(c);
            }
        }
    }

    private void appendFragment(String fragment) {
        builder.append("#");
        for (int i = 0; i < fragment.length(); ++i) {
            char c = fragment.charAt(i);
            if (!inAllowedSet(c) || inFragmentPercentEncodeSet(c)) {
                appendPercentEncoded(c);
            } else {
                builder.append(c);
            }
        }
    }

    private final void appendPercentEncoded(char c) {
        String str = String.valueOf(c);
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        for (byte b: bytes) {
            builder.append("%");
            builder.append(HEXCHARS[(b >>> 4) & 0x0f]);
            builder.append(HEXCHARS[b & 0x0f]);
        }
    }

    private static boolean inAllowedSet(char c) {
        return ALLOWED_CHAR_SET.get(c);
    }
    
    private static boolean isUserinfoPercentEncodeSet(char c) {
        return inPathPercentEncodeSet(c) ||
               c == '/' ||
               c == ':' ||
               c == ';' ||
               c == '=' ||
               c == '@' ||
               c == '[' ||
               c == '\\' ||
               c == ']' ||
               c == '^' ||
               c == '|'
               ;
    }
    
    private static boolean inPathPercentEncodeSet(char c) {
        return inFragmentPercentEncodeSet(c) ||
                c == '#' ||
                c == '?' ||
                c == '{' ||
                c == '}'
                ;
    }

    private static boolean inQueryPercentEncodeSet(char c) {
        return  c == '\u0020' ||
                c == '\"' ||
                c == '#' ||
                c == '<' ||
                c == '>' ||
                c == '[' ||
                c == ']'
                ;
    }

    private static boolean inFragmentPercentEncodeSet(char c) {
        return  c == '\u0020' ||
                c == '\"' ||
                c == '<' ||
                c == '>' ||
                c == '`' ||
                c == '[' ||
                c == ']'
                ;
    }
}
