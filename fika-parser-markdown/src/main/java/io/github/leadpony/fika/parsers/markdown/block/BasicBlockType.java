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
    /**
     * Setext heading. 
     * This must have higher precedence than thematic break.
     */
    SETEXT_HEADING(100),
    THEMATIC_BREAK(110),
    LIST(120),
    LIST_ITEM(130),
    ATX_HEADING(140),
    BLOCK_QUOTE(150),
    FENCED_CODE(160),
    HTML_BLOCK(170),
    INDENTED_CODE(180),
    LINK_DEFINITION(190),
    PARAGRAPH(200),
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
