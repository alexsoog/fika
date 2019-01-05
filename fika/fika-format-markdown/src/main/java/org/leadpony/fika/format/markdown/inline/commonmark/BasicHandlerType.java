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
package org.leadpony.fika.format.markdown.inline.commonmark;

import org.leadpony.fika.format.markdown.inline.HandlerType;

/**
 * @author leadpony
 */
enum BasicHandlerType implements HandlerType {
    
    ASTERISK_EMPHASIS(100),
    AUTOLINK(100),
    BACKSLASH_ESCAPE(200),
    BACKSLASH_LINE_BREAK(100),
    CHARACTER_REFERENCE(200),
    CODE_SPAN(100),
    IMAGE(100),
    HARD_LINE_BREAK(100),
    LINK(100),
    LINK_CLOSER(100),
    RAW_HTML(110),
    UNDERSCORE_EMPHASIS(100)
    ;

    private final int precedence;
    
    private BasicHandlerType(int precedence) {
        this.precedence = precedence;
    }
    
    @Override
    public int precedence() {
        return precedence;
    }
}
