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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.leadpony.fika.core.model.Block;
import io.github.leadpony.fika.core.model.HtmlBlock;

/**
 * @author leadpony
 */
class HtmlBlockMatcherFactory implements BlockMatcherFactory {

    private static final List<Function<BlockInputSequence, HtmlBlockMatcher>> starters = new ArrayList<>();
    private static final List<Function<BlockInputSequence, HtmlBlockMatcher>> interruptingStarters = new ArrayList<>();
    
    static {
        interruptingStarters.add(ScriptMatcher::start);
        interruptingStarters.add(CommentMatcher::start);
        interruptingStarters.add(PIMatcher::start);
        interruptingStarters.add(DtdMatcher::start);
        interruptingStarters.add(CDataMatcher::start);
        interruptingStarters.add(ElementMatcher::start);
        starters.addAll(interruptingStarters);
        starters.add(CompleteTagMatcher::start);
    }
    
    public HtmlBlockMatcherFactory() {
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
    public BlockMatcher newMatcher(BlockInputSequence content) {
        return newMatcher(content, starters);
    }
    
    @Override
    public BlockMatcher newInterrupter(BlockInputSequence content, BlockMatcher current) {
        return newMatcher(content, interruptingStarters);
    }

    private BlockMatcher newMatcher(BlockInputSequence content, List<Function<BlockInputSequence, HtmlBlockMatcher>> functions) {
        int i = content.countLeadingSpaces(0, 3);
        if (i >= content.length() || content.charAt(i) != '<') {
            return null;
        }
        content = content.subContent(i);
        for (Function<BlockInputSequence, HtmlBlockMatcher> function: functions) {
            BlockMatcher matcher = function.apply(content);
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
        protected Block buildBlock() {
            HtmlBlock block = nodeFactory().newHtmlBlock();
            block.setHtml(builder.toString());
            return block;
        }
        
        protected void appendLine(BlockInputSequence content) {
            this.builder.append(content.toSourceString()).append('\n');
        }
    }

    private static class ScriptMatcher extends HtmlBlockMatcher {
    
        private static final Pattern START_PATTERN = Pattern.compile(
                "^<(script|pre|style)(\\u0020|>|$)", Pattern.CASE_INSENSITIVE);
        
        private static final Pattern END_PATTERN = Pattern.compile(
                "</(script|pre|style)>", Pattern.CASE_INSENSITIVE);
    
        static HtmlBlockMatcher start(BlockInputSequence content) {
            if (START_PATTERN.matcher(content).find()) {
                return new ScriptMatcher();
            }
            return null;
        }
        
        @Override
        public Result match(BlockInputSequence content) {
            appendLine(content);
            if (END_PATTERN.matcher(content).find()) {
                return Result.COMPLETED;
            }
            return Result.CONTINUED;
        }
    }

    private static class CommentMatcher extends HtmlBlockMatcher {
        
        static HtmlBlockMatcher start(BlockInputSequence content) {
            if (content.startsWith("<!--")) {
                return new CommentMatcher();
            }
            return null;
        }
        
        @Override
        public Result match(BlockInputSequence content) {
            appendLine(content);
            return content.contains("-->") ? Result.COMPLETED : Result.CONTINUED;
        }
    }
    
    private static class PIMatcher extends HtmlBlockMatcher {
        
        static HtmlBlockMatcher start(BlockInputSequence content) {
            if (content.startsWith("<?")) {
                return new PIMatcher();
            }
            return null;
        }
        
        @Override
        public Result match(BlockInputSequence content) {
            appendLine(content);
            return content.contains("?>") ? Result.COMPLETED : Result.CONTINUED;
        }
    }

    private static class DtdMatcher extends HtmlBlockMatcher {
        
        private static final Pattern START_PATTERN = Pattern.compile("^<![A-Z]");

        static HtmlBlockMatcher start(BlockInputSequence content) {
            if (START_PATTERN.matcher(content).find()) {
                return new DtdMatcher();
            }
            return null;
        }
        
        @Override
        public Result match(BlockInputSequence content) {
            appendLine(content);
            return content.contains(">") ? Result.COMPLETED : Result.CONTINUED;
        }
    }

    private static class CDataMatcher extends HtmlBlockMatcher {
        
        static HtmlBlockMatcher start(BlockInputSequence content) {
            if (content.startsWith("<![CDATA[")) {
                return new CDataMatcher();
            }
            return null;
        }
        
        @Override
        public Result match(BlockInputSequence content) {
            appendLine(content);
            return content.contains("]]>") ? Result.COMPLETED : Result.CONTINUED;
        }
    }

    private static class ElementMatcher extends HtmlBlockMatcher {
    
        private static final Pattern START_PATTERN = Pattern.compile(
                "^("
                + "(<(\\w+)(\\u0020|$|>|/>))"
                + "|"
                + "(</(\\w+)(\\u0020|$|>))"
                + ")"
                );
        
        @SuppressWarnings("serial")
        private static final Set<String> ELEMENTS_ALLOWED = new HashSet<String>() {{
            add("address");
            add("article");
            add("aside");
            add("base");
            add("basefont");
            add("blockquote");
            add("body");
            add("caption");
            add("center");
            add("col");
            add("colgroup");
            add("dd");
            add("details");
            add("dialog");
            add("dir");
            add("div");
            add("dl");
            add("dt");
            add("fieldset");
            add("figcaption");
            add("figure");
            add("footer");
            add("form");
            add("frame");
            add("frameset");
            add("h1");
            add("h2");
            add("h3");
            add("h4");
            add("h5");
            add("h6");
            add("head");
            add("header");
            add("hr");
            add("html");
            add("iframe");
            add("legend");
            add("li");
            add("link");
            add("main");
            add("menu");
            add("menuitem");
            add("meta");
            add("nav");
            add("noframes");
            add("ol");
            add("optgroup");
            add("option");
            add("p");
            add("param");
            add("section");
            add("source");
            add("summary");
            add("table");
            add("tbody");
            add("td");
            add("tfoot");
            add("th");
            add("thead");
            add("title");
            add("tr");
            add("track");
            add("ul");
        }};
        
        static HtmlBlockMatcher start(BlockInputSequence content) {
            Matcher m = START_PATTERN.matcher(content);
            if (!m.find()) {
                return null;
            }
            String tag = m.group(3);
            if (tag == null) {
                tag = m.group(6);
            }
            if (ELEMENTS_ALLOWED.contains(tag.toLowerCase())) {
                return new ElementMatcher();
            }
            return null;
        }
        
        @Override
        public Result match(BlockInputSequence content) {
            if (content.isBlank()) {
                return Result.COMPLETED;
            }
            appendLine(content);
            return Result.CONTINUED;
        }
    }

    private static class CompleteTagMatcher extends HtmlBlockMatcher {
        
        private static final Pattern START_PATTERN = Pattern.compile(
                "^(" +
                "(<(\\w+)(\\s*\\w+?\\s*=\\s*\".*?\")*\\s*>)|" +
                "(</(\\w+)\\s*>)" +        
                ")\\u0020*$"
                );
        
        @SuppressWarnings("serial")
        private static final Set<String> ELEMENTS_NOT_ALLOWED = new HashSet<String>() {{
            add("script");
            add("pre");
            add("style");
        }};
        
        static HtmlBlockMatcher start(BlockInputSequence content) {
            Matcher m = START_PATTERN.matcher(content);
            if (!m.find()) {
                return null;
            }
            String tag = m.group(3);
            if (tag == null) {
                tag = m.group(6);
            }
            if (!ELEMENTS_NOT_ALLOWED.contains(tag.toLowerCase())) {
                return new CompleteTagMatcher();
            }
            return null;
        }
        
        @Override
        public Result match(BlockInputSequence content) {
            if (content.isBlank()) {
                return Result.COMPLETED;
            }
            appendLine(content);
            return Result.CONTINUED;
        }
    }
}
