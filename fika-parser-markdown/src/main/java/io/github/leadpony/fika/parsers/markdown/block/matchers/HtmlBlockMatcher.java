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
package io.github.leadpony.fika.parsers.markdown.block.matchers;

import static io.github.leadpony.fika.parsers.markdown.common.Characters.isWhitespace;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.leadpony.fika.core.model.Block;
import io.github.leadpony.fika.parsers.markdown.block.AbstractBlockMatcher;
import io.github.leadpony.fika.parsers.markdown.block.BasicBlockType;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcher;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcherFactory;
import io.github.leadpony.fika.parsers.markdown.block.BlockType;
import io.github.leadpony.fika.parsers.markdown.common.HtmlMatchers;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;

/**
 * @author leadpony
 */
class HtmlBlockMatcher extends AbstractBlockMatcher {
    
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
        return getNodeFactory().newHtmlBlock(builder.toString());
    }
    
    protected void appendLine(InputSequence input) {
        this.builder.append(input.toSourceString()).append('\n');
    }
}

/**
 * @author leadpony
 */
class ScriptMatcher extends HtmlBlockMatcher {

    private static final Pattern START_PATTERN = Pattern.compile(
            "^<(script|pre|style)(\\u0020|>|$)", Pattern.CASE_INSENSITIVE);
    
    private static final Pattern END_PATTERN = Pattern.compile(
            "</(script|pre|style)>", Pattern.CASE_INSENSITIVE);

    static HtmlBlockMatcher start(InputSequence input) {
        if (START_PATTERN.matcher(input).find()) {
            return new ScriptMatcher();
        }
        return null;
    }
    
    @Override
    public Result match(InputSequence input) {
        appendLine(input);
        if (END_PATTERN.matcher(input).find()) {
            return Result.COMPLETED;
        }
        return Result.CONTINUED;
    }
}

/**
 * @author leadpony
 */
class CommentMatcher extends HtmlBlockMatcher {
    
    static HtmlBlockMatcher start(InputSequence input) {
        if (input.startsWith("<!--")) {
            return new CommentMatcher();
        }
        return null;
    }
    
    @Override
    public Result match(InputSequence input) {
        appendLine(input);
        return input.contains("-->") ? Result.COMPLETED : Result.CONTINUED;
    }
}

/**
 * @author leadpony
 */
class PIMatcher extends HtmlBlockMatcher {
    
    static HtmlBlockMatcher start(InputSequence input) {
        if (input.startsWith("<?")) {
            return new PIMatcher();
        }
        return null;
    }
    
    @Override
    public Result match(InputSequence input) {
        appendLine(input);
        return input.contains("?>") ? Result.COMPLETED : Result.CONTINUED;
    }
}

/**
 * @author leadpony
 */
class DtdMatcher extends HtmlBlockMatcher {
    
    private static final Pattern START_PATTERN = Pattern.compile("^<![A-Z]");

    static HtmlBlockMatcher start(InputSequence input) {
        if (START_PATTERN.matcher(input).find()) {
            return new DtdMatcher();
        }
        return null;
    }
    
    @Override
    public Result match(InputSequence input) {
        appendLine(input);
        return input.contains(">") ? Result.COMPLETED : Result.CONTINUED;
    }
}

/**
 * @author leadpony
 */
class CDataMatcher extends HtmlBlockMatcher {
    
    static HtmlBlockMatcher start(InputSequence input) {
        if (input.startsWith("<![CDATA[")) {
            return new CDataMatcher();
        }
        return null;
    }
    
    @Override
    public Result match(InputSequence input) {
        appendLine(input);
        return input.contains("]]>") ? Result.COMPLETED : Result.CONTINUED;
    }
}

/**
 * @author leadpony
 */
class ElementMatcher extends HtmlBlockMatcher {

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
    
    static HtmlBlockMatcher start(InputSequence input) {
        Matcher m = START_PATTERN.matcher(input);
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
    public Result match(InputSequence input) {
        if (input.isBlank()) {
            return Result.COMPLETED;
        }
        appendLine(input);
        return Result.CONTINUED;
    }
}

/**
 * type 7: 
 * Line begins with a complete open tag or closing tag
 * (with any tag name other than script, style, or pre)
 * followed only by whitespace or the end of the line.
 * 
 * @author leadpony
 */
class CompleteHtmlTagMatcher extends HtmlBlockMatcher {
    
    @SuppressWarnings("serial")
    private static final Set<String> ELEMENTS_NOT_ALLOWED = new HashSet<String>() {{
        add("script");
        add("pre");
        add("style");
    }};
    
    static HtmlBlockMatcher start(InputSequence input) {
        Matcher m = HtmlMatchers.newTagLineMatcher(input);
        if (!m.matches()) {
            return null;
        }
        String tag = extractTagName(m.group());
        if (!ELEMENTS_NOT_ALLOWED.contains(tag.toLowerCase())) {
            return new CompleteHtmlTagMatcher();
        }
        return null;
    }
    
    @Override
    public Result match(InputSequence input) {
        if (input.isBlank()) {
            return Result.COMPLETED;
        }
        appendLine(input);
        return Result.CONTINUED;
    }
    
    private static String extractTagName(String tag) {
        int i = 1;
        int beginIndex = 1;
        if (tag.charAt(i) == '/') {
            beginIndex = 2;
            ++i;
        }
        for (; i < tag.length(); ++i) {
            char c = tag.charAt(i);
            if (isWhitespace(c) || c == '>' || c == '/') {
                break;
            }
        }
        return tag.substring(beginIndex, i);
    }
}

/**
 * @author leadpony
 */
class HtmlBlockMatcherFactory implements BlockMatcherFactory {

    private static final List<Function<InputSequence, HtmlBlockMatcher>> starters = new ArrayList<>();
    private static final List<Function<InputSequence, HtmlBlockMatcher>> interruptingStarters = new ArrayList<>();
    
    static {
        interruptingStarters.add(ScriptMatcher::start);
        interruptingStarters.add(CommentMatcher::start);
        interruptingStarters.add(PIMatcher::start);
        interruptingStarters.add(DtdMatcher::start);
        interruptingStarters.add(CDataMatcher::start);
        interruptingStarters.add(ElementMatcher::start);
        starters.addAll(interruptingStarters);
        starters.add(CompleteHtmlTagMatcher::start);
    }
    
    public HtmlBlockMatcherFactory() {
    }

    @Override
    public BlockType blockType() {
        return BasicBlockType.HTML_BLOCK;
    }

    @Override
    public Set<? extends BlockType> interruptible() {
        return EnumSet.of(BasicBlockType.PARAGRAPH, BasicBlockType.LINK_DEFINITION);
    }
    
    @Override
    public BlockMatcher newMatcher(InputSequence input) {
        return newMatcher(input, starters);
    }
    
    @Override
    public BlockMatcher newInterrupter(InputSequence input, BlockMatcher current) {
        return newMatcher(input, interruptingStarters);
    }

    private BlockMatcher newMatcher(InputSequence input, List<Function<InputSequence, HtmlBlockMatcher>> functions) {
        int i = input.countLeadingSpaces(0, 3);
        if (i >= input.length() || input.charAt(i) != '<') {
            return null;
        }
        input = input.subSequence(i);
        for (Function<InputSequence, HtmlBlockMatcher> function: functions) {
            BlockMatcher matcher = function.apply(input);
            if (matcher != null) {
                return matcher;
            }
        }
        return null;
    }
}
