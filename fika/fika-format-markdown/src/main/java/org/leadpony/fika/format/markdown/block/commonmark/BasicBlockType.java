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
package org.leadpony.fika.format.markdown.block.commonmark;

import org.leadpony.fika.format.markdown.block.BlockType;

/**
 * Predefined block types.
 * 
 * @author leadpony
 */
public enum BasicBlockType implements BlockType {

    SETEXT_HEADING(100),
    THEMATIC_BREAK(110),
    BULLET_LIST(120),
    ORDERED_LIST(120),
    BULLET_LIST_ITEM(130),
    ORDERED_LIST_ITEM(130),
    TERM_DEFINITION_LIST(140),
    TERM(150),
    TERM_DEFINITIION(160),
    ATX_HEADING(170),
    BLOCK_QUOTE(180),
    FENCED_CODE(190),
    ADMONITION(200),
    HTML_BLOCK(210),
    INDENTED_CODE(220),
    LINK_DEFINITION(230),
    PARAGRAPH(300)
    ;
    
    private final int precedence;
    
    private BasicBlockType(int precedence) {
        this.precedence = precedence;
    }

    public int precedence() {
        return precedence;
    }
}
