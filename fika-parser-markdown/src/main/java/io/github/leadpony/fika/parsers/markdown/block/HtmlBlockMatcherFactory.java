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
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

import io.github.leadpony.fika.core.nodes.Node;
import io.github.leadpony.fika.core.parser.support.nodes.SimpleHtmlBlock;

/**
 * @author leadpony
 */
class HtmlBlockMatcherFactory implements BlockMatcherFactory {

    private final List<Function<Content, HtmlBlockMatcher>> entries = new ArrayList<>();
    
    HtmlBlockMatcherFactory() {
        entries.add(ScriptBlockMatcher::start);
    }

    @Override
    public BlockType blockType() {
        return BasicBlockType.HTML_BLOCK;
    }

    @Override
    public Set<? extends BlockType> interruptible() {
        return EnumSet.of(BasicBlockType.PARAGRAPH);
    }
    
    @Override
    public BlockMatcher newMatcher(Content content) {
        for (Function<Content, HtmlBlockMatcher> entry: entries) {
            BlockMatcher matcher = entry.apply(content);
            if (matcher != null) {
                return matcher;
            }
        }
        return null;
    }

    private static class HtmlBlockMatcher extends AbstractBlockMatcher {
        
        private final StringBuilder builder;
     
        protected HtmlBlockMatcher() {
            this.builder = new StringBuilder();
        }
    
        @Override
        public BlockType blockType() {
            return BasicBlockType.HTML_BLOCK;
        }
    
        @Override
        protected Node buildNode() {
            return new SimpleHtmlBlock(builder.toString());
        }
        
        protected void appendLine(Content content) {
            this.builder.append(content.toOriginalString()).append('\n');
        }
    }

    private static class ScriptBlockMatcher extends HtmlBlockMatcher {
    
        private static final Pattern START_PATTERN = Pattern.compile(
                "^<(script|pre|style)(\\u0020|>|$)", Pattern.CASE_INSENSITIVE);
        
        private static final Pattern END_PATTERN = Pattern.compile(
                "</(script|pre|style)>", Pattern.CASE_INSENSITIVE);
    
        static HtmlBlockMatcher start(Content content) {
            if (START_PATTERN.matcher(content).find()) {
                return new ScriptBlockMatcher();
            }
            return null;
        }
        
        @Override
        public Result match(Content content) {
            appendLine(content);
            if (END_PATTERN.matcher(content).find()) {
                return Result.COMPLETED;
            }
            return Result.CONTINUED;
        }
    }
}
