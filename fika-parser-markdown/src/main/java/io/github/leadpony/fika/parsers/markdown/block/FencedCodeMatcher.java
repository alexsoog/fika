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

import io.github.leadpony.fika.core.nodes.Node;
import io.github.leadpony.fika.core.parser.support.nodes.SimpleCodeBlock;

/**
 * @author leadpony
 */
class FencedCodeMatcher extends AbstractBlockMatcher {

    private final int indentSize;
    private final char fenceChar;
    private final int fenceLength;
    private final String infoString;
    
    private final StringBuilder builder;
    
    static Factory factory() {
        return Factory.instance;
    }
    
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
    public Result match(Content content) {
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
    protected Node buildNode() {
        return new SimpleCodeBlock(builder.toString(), infoString);
    }

    private boolean testClosingFence(Content content) {
        int i = content.countSpaces(0, 3);
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
    
    private void appendLine(Content content) {
        if (indentSize > 0) {
            content = content.removeIndentUpTo(indentSize);
        }
        builder.append(content.toOriginalString()).append('\n');
    }
    
    static class Factory implements BlockMatcher.Factory {

        private static final Factory instance = new Factory();
        
        @Override
        public BlockType blockType() {
            return BasicBlockType.FENCED_CODE;
        }
        
        @Override
        public BlockMatcher newMatcher(Content content, BlockMatcher current) {
            int indentSize = content.countSpaces(0, 3);
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
            if (infoString != null && infoString.contains("`")) {
                return null;
            }
            return new FencedCodeMatcher(indentSize, fenceChar, fenceLength, infoString);
        }
        
        private String extractInfoString(Content content, int offset) {
            int i = offset;
            while (i < content.length() && content.charAt(i) == SPACE) {
                ++i;
            }
            if (i >= content.length()) {
                return null;
            }
            int start = i++;
            while (i < content.length() && content.charAt(i) != SPACE) {
                ++i;
            }
            return content.subContent(start, i).toOriginalString();
        }
    }
}
