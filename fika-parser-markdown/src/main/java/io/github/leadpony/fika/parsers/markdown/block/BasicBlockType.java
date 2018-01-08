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
package io.github.leadpony.fika.parsers.markdown.block;

/**
 * @author leadpony
 *
 */
public enum BasicBlockType implements BlockType {
    THEMATIC_BREAK(100),
    LIST(110),
    LIST_ITEM(110),
    HEADING(120),
    BLOCK_QUOTE(130),
    FENCED_CODE(140),
    HTML_BLOCK(150),
    INDENTED_CODE(160),
    LINK_DEFINITION(170),
    PARAGRAPH(180),
    DOCUMENT,
    ;

    private final int precedence;
    
    private BasicBlockType() {
        this.precedence = Integer.MAX_VALUE;
    }
    
    private BasicBlockType(int precedence) {
        this.precedence = precedence;
    }
    
    @Override
    public int precedence() {
        return precedence;
    }
}
