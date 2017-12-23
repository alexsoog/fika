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

import io.github.leadpony.fika.core.nodes.Heading;
import io.github.leadpony.fika.core.parser.support.nodes.SimpleHeading;
import io.github.leadpony.fika.core.parser.support.nodes.SimpleText;

/**
 * @author leadpony
 */
class HeadingMatcher extends AbstractBlockMatcher {

    private static final int MAX_LEVEL = 6;
    
    private final int level;
    private final String title;
  
    static Factory factory() {
        return Factory.instance;
    }
    
    private HeadingMatcher(int level, String title) {
        this.level = level;
        this.title = title;
    }

    @Override
    public BlockType blockType() {
        return BasicBlockType.HEADING;
    }
    
    @Override
    public Result match(Content content) {
        return Result.COMPLETED;
    }
    
    @Override
    protected Heading buildNode() {
        SimpleHeading node = new SimpleHeading(this.level);
        SimpleText text = new SimpleText(this.title);
        node.childNodes().add(text);
        context().addInline(text);
        return node;
    }
    
    private static String extractTitle(Content content) {
        content = content.trimSpaces();
        content = trimClosingSequenceOfHash(content);
        return content.trimSpaces().toOriginalString();
    }
    
    private static Content trimClosingSequenceOfHash(Content content) {
        if (content.length() == 0) {
            return content;
        }
        int i = content.length() - 1;
        char c = content.charAt(i);
        if (c != '#') {
            return content;
        }
        while (i >= 0) {
            c = content.charAt(i);
            if (c != '#') {
                break;
            }
            --i;
        }
        if (i < 0) {
            return content.subContent(0, 0);
        }
        c = content.charAt(i);
        if (c == '\u0020' || c == '\t') {
            return content.subContent(0, i);
        } else {
            return content;
        }
    }
    
    static class Factory implements BlockMatcher.Factory {
        
        private static final Factory instance = new Factory();
        
        @Override
        public BlockType blockType() {
            return BasicBlockType.HEADING;
        }

        @Override
        public BlockMatcher newMatcher(Content content) {
            int i = content.countSpaces(0, 3);
            int level = 0;
            for (; i < content.length(); i++) {
                if (content.charAt(i) != '#') {
                    break;
                }
                if (++level > MAX_LEVEL) {
                    return null;
                }
            }
            if (level == 0) {
                return null;
            }
            if (i < content.length()) {
                // Checks the first letter after #.
                char c = content.charAt(i++);
                if (c != '\u0020' && c != '\t') {
                    return null;
                }
            }
            return new HeadingMatcher(level, extractTitle(content.subContent(i)));
        }
    }
}
