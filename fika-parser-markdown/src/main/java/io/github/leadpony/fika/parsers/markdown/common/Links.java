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

/**
 * @author leadpony
 *
 */
public final class Links {
   
    public static final String LINK_DESTINATION =
            "(?<destination><(\\[<>]|[^\u0020\n<>])*>|(\\\\[()]|[^\u0020\\p{Cntrl}])+)";
 
    public static final String LINK_TITLE =
            "(?<title>" +
            "(\"(\\\\\"|[^\"])*\")|" +
            "('(\\\\'|[^'])*')|" +
            "(\\((\\\\\\)|[^\\)])*\\))" +
            ")";
 
    public static final String INLINE_LINK =
            "\\(\\s*" +
            LINK_DESTINATION + "?" +
           "(\\s+" + LINK_TITLE + ")?" +        
            "\\s*\\)";
   
    public static final String LINK_LABEL =
            "\\[(?<label>(\\\\\\]|[^\\]])+)\\]:";

    public static final String LINK_DEFINITION =
            "^\\p{Blank}*" + LINK_LABEL +
            "\\s*" + LINK_DESTINATION +
            "(\\s+" + LINK_TITLE + ")?" +
            "\\s*($|\\n)";

    private Links() {
    }
}
