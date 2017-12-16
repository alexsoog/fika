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

import io.github.leadpony.fika.core.nodes.Paragraph;
import io.github.leadpony.fika.core.parser.helper.nodes.SimpleParagraph;

/**
 * @author leadpony
 */
class ParagraphMatcher extends AbstractBlockMatcher {
    
    private static final int PRECEDENCE = 2;
    
    private final StringBuilder builder;
    private int lines;

    ParagraphMatcher(Content content) {
        this.builder = new StringBuilder(extractContent(content));
        this.lines = 0;
    }
    
    @Override
    public boolean isInterruptible() {
        return true;
    }
    
    @Override
    public int precedence() {
        return PRECEDENCE;
    }
    
    @Override
    public boolean match(Content content) {
        if (lines++ == 0) {
            return true;
        }
        if (content.isBlank()) {
            return false;
        }
        builder.append('\n').append(extractContent(content));
        return true;
    }

    @Override
    public Paragraph close() {
        SimpleParagraph node = new SimpleParagraph();
        context().addInline(node, builder.toString());
        return node;
    }
    
    private static String extractContent(Content content) {
        return content.trimLeadingSpaces().toString();
    }
}
