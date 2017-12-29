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

import java.util.ArrayList;
import java.util.List;

import io.github.leadpony.fika.core.nodes.CodeBlock;

/**
 * @author leadpony
 */
class IndentedCodeMatcherFactory implements BlockMatcherFactory {

    private static final int INDENT_SIZE = 4;

    @Override
    public BlockType blockType() {
        return BasicBlockType.INDENTED_CODE;
    }
    
    @Override
    public BlockMatcher newMatcher(Content content) {
        if (!content.hasIndent(INDENT_SIZE)) {
            return null;
        }
        return new IndentedCodeMatcher();
    }
    
    private static class IndentedCodeMatcher extends AbstractBlockMatcher {
    
        private final List<String> lines;
        private int lastNonBlankLineNo;
        
    
        private IndentedCodeMatcher() {
            this.lines = new ArrayList<>();
        }
    
        @Override
        public BlockType blockType() {
            return BasicBlockType.INDENTED_CODE;
        }
        
        @Override
        public Result match(Content content) {
            if (lineNo() <= 1 || content.hasIndent(INDENT_SIZE)) {
                appendLine(content);
                return Result.CONTINUED;
            } else if (content.isBlank()) {
                appendBlank();
                return Result.CONTINUED;
            }
            return Result.NOT_MATCHED;
        }
    
        @Override
        protected CodeBlock buildBlock() {
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i <= lastNonBlankLineNo; ++i) {
                builder.append(lines.get(i - 1)).append('\n');
            }
            CodeBlock block = nodeFactory().newCodeBlock();
            block.setContent(builder.toString());
            return block;
        }
    
        private void appendLine(Content content) {
            content = content.subContent(INDENT_SIZE);
            this.lines.add(content.toOriginalString());
            if (!content.isBlank()) {
                this.lastNonBlankLineNo = lineNo();
            }
        }
        
        private void appendBlank() {
            this.lines.add("");
        }
    }
}
