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

import static io.github.leadpony.fika.parsers.markdown.base.Characters.SPACE;

import java.util.EnumSet;
import java.util.Set;

import io.github.leadpony.fika.core.nodes.Block;
import io.github.leadpony.fika.core.nodes.CodeBlock;

/**
 * @author leadpony
 */
class FencedCodeMatcherFactory implements BlockMatcherFactory { 
    
    FencedCodeMatcherFactory() {
    }

    @Override
    public BlockType blockType() {
        return BasicBlockType.FENCED_CODE;
    }
    
    @Override
    public Set<? extends BlockType> interruptible() {
        return EnumSet.of(BasicBlockType.PARAGRAPH);
    }

    @Override
    public BlockMatcher newMatcher(BlockInputSequence content) {
        int indentSize = content.countLeadingSpaces(0, 3);
        int i = indentSize;
        char fenceChar = content.charAt(i);
        if (fenceChar != '`' && fenceChar != '~') {
            return null;
        }
        int fenceLength = 1;
        while (++i < content.length()) {
            char c = content.charAt(i);
            if (c != fenceChar) {
                break;
            }
            ++fenceLength;
        }
        if (fenceLength < 3) {
            return null;
        }
        String infoString = extractInfoString(content, i);
        if (infoString.contains("`")) {
            return null;
        }
        return new FencedCodeMatcher(indentSize, fenceChar, fenceLength, infoString);
    }

    @Override
    public BlockMatcher newInterrupter(BlockInputSequence content, BlockMatcher current) {
        return newMatcher(content);
    }
    
    private String extractInfoString(BlockInputSequence content, int offset) {
        return content.subContent(offset).trimSpaces().toSourceString();
    }
    
    private static class FencedCodeMatcher extends AbstractBlockMatcher {
    
        private final int indentSize;
        private final char fenceChar;
        private final int fenceLength;
        private final String infoString;
        
        private final StringBuilder builder;
        
        private FencedCodeMatcher(int indentSize, char fenceChar, int fenceLength, String infoString) {
            this.indentSize = indentSize;
            this.fenceChar = fenceChar;
            this.fenceLength = fenceLength;
            this.infoString = infoString;
            this.builder = new StringBuilder();
        }
      
        @Override
        public BlockType blockType() {
            return BasicBlockType.FENCED_CODE;
        }
        
        @Override
        public Result match(BlockInputSequence content) {
            if (lineNo() <= 1) {
                return Result.CONTINUED;
            }
            if (testClosingFence(content)) {
                return Result.COMPLETED;
            }
            appendLine(content);
            return Result.CONTINUED;
        }
    
        @Override
        protected Block buildBlock() {
            CodeBlock block = nodeFactory().newCodeBlock();
            block.setContent(builder.toString());
            String[] words = infoString.split("\\s+");
            String language = words[0];
            if (!language.isEmpty()) {
                block.setLanguage(language);
            }
            return block;
        }
    
        private boolean testClosingFence(BlockInputSequence content) {
            int i = content.countLeadingSpaces(0, 3);
            if (i >= content.length()) {
                return false;
            }
            char c = content.charAt(i);
            if (c != fenceChar) {
                return false;
            }
            int length = 1;
            while (++i < content.length()) {
                c = content.charAt(i);
                if (c == fenceChar) {
                    ++length;    
                } else if (c == SPACE) {
                    break;
                } else {
                    return false;
                }
            }
            if (length < fenceLength) {
                return false;
            }
            while (++i < content.length()) {
                c = content.charAt(i);
                if (c != SPACE) {
                    return false;
                }
            }
            return true;
        }
        
        private void appendLine(BlockInputSequence content) {
            if (indentSize > 0) {
                content = content.removeIndentUpTo(indentSize);
            }
            builder.append(content.toSourceString()).append('\n');
        }
    }
}
