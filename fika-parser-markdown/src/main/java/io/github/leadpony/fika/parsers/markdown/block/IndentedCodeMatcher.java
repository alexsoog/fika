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

import io.github.leadpony.fika.core.nodes.CodeBlock;
import io.github.leadpony.fika.core.parser.helper.nodes.SimpleCodeBlock;

/**
 * @author leadpony
 */
public class IndentedCodeMatcher extends AbstractBlockMatcher {

    private final StringBuilder builder;
    private int lines;
    
    private static final int INDENT_SIZE = 4;

    private IndentedCodeMatcher() {
        this.builder = new StringBuilder();
    }

    @Override
    public boolean match(Content content) {
        if (this.lines == 0 || testLine(content)) {
            appendLine(content);
            return true;
        }
        return false;
    }

    @Override
    public CodeBlock close() {
        return new SimpleCodeBlock(builder.toString());
    }
    
    private void appendLine(Content content) {
        content = content.subcontent(INDENT_SIZE);
        this.builder.append(content.toString()).append("\n");
        this.lines++;
    }
    
    private static boolean testLine(Content content) {
        int spaces = 0;
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '\u0020' || c == '\t') {
                if (++spaces >= 4) {
                    return true;
                }
            } else {
                break;
            }
        }
        return false;
    }

    public static class Factory implements BlockMatcher.Factory {

        @Override
        public BlockMatcher newMatcher(Content content) {
            if (!testLine(content)) {
                return null;
            }
            return new IndentedCodeMatcher();
        }
    }
}
