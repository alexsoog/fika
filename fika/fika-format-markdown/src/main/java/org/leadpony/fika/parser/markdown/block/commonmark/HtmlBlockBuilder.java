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
package org.leadpony.fika.parser.markdown.block.commonmark;

import static org.leadpony.fika.parser.markdown.common.Characters.isWhitespace;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.leadpony.fika.parser.markdown.block.AbstractBlockBuilder;
import org.leadpony.fika.parser.markdown.block.BlockType;
import org.leadpony.fika.parser.markdown.common.HtmlMatchers;
import org.leadpony.fika.parser.markdown.common.InputSequence;
import org.leadpony.fika.parser.model.Block;

/**
 * @author leadpony
 */
class HtmlBlockBuilder extends AbstractBlockBuilder {
    
    private final StringBuilder builder;
 
    protected HtmlBlockBuilder() {
        this.builder = new StringBuilder();
    }

    @Override
    public BlockType blockType() {
        return BasicBlockType.HTML_BLOCK;
    }

    @Override
    protected Block buildBlock() {
        return getNodeFactory().createHtmlBlock(builder.toString());
    }
    
    protected void accumelateLine(InputSequence input) {
        this.builder.append(input.toSourceString()).append('\n');
    }
}

/**
 * @author leadpony
 */
class HtmlScriptBlockBuilder extends HtmlBlockBuilder {

    private static final Pattern START_PATTERN = Pattern.compile(
            "^<(script|pre|style)(\\u0020|>|$)", Pattern.CASE_INSENSITIVE);
    
    private static final Pattern END_PATTERN = Pattern.compile(
            "</(script|pre|style)>", Pattern.CASE_INSENSITIVE);

    static HtmlBlockBuilder start(InputSequence input) {
        if (START_PATTERN.matcher(input).find()) {
            return new HtmlScriptBlockBuilder();
        }
        return null;
    }
    
    @Override
    public Result processLine(InputSequence input) {
        accumelateLine(input);
        if (END_PATTERN.matcher(input).find()) {
            return Result.COMPLETED;
        }
        return Result.CONTINUED;
    }
}

/**
 * @author leadpony
 */
class HtmlCommentBlockBuilder extends HtmlBlockBuilder {
    
    static HtmlBlockBuilder start(InputSequence input) {
        if (input.startsWith("<!--")) {
            return new HtmlCommentBlockBuilder();
        }
        return null;
    }
    
    @Override
    public Result processLine(InputSequence input) {
        accumelateLine(input);
        return input.contains("-->") ? Result.COMPLETED : Result.CONTINUED;
    }
}

/**
 * @author leadpony
 */
class ProcessingInstructionBuilder extends HtmlBlockBuilder {
    
    static HtmlBlockBuilder start(InputSequence input) {
        if (input.startsWith("<?")) {
            return new ProcessingInstructionBuilder();
        }
        return null;
    }
    
    @Override
    public Result processLine(InputSequence input) {
        accumelateLine(input);
        return input.contains("?>") ? Result.COMPLETED : Result.CONTINUED;
    }
}

/**
 * @author leadpony
 */
class DeclarationBuilder extends HtmlBlockBuilder {
    
    private static final Pattern START_PATTERN = Pattern.compile("^<![A-Z]");

    static HtmlBlockBuilder start(InputSequence input) {
        if (START_PATTERN.matcher(input).find()) {
            return new DeclarationBuilder();
        }
        return null;
    }
    
    @Override
    public Result processLine(InputSequence input) {
        accumelateLine(input);
        return input.contains(">") ? Result.COMPLETED : Result.CONTINUED;
    }
}

/**
 * @author leadpony
 */
class CDataSectionBuilder extends HtmlBlockBuilder {
    
    static HtmlBlockBuilder start(InputSequence input) {
        if (input.startsWith("<![CDATA[")) {
            return new CDataSectionBuilder();
        }
        return null;
    }
    
    @Override
    public Result processLine(InputSequence input) {
        accumelateLine(input);
        return input.contains("]]>") ? Result.COMPLETED : Result.CONTINUED;
    }
}

/**
 * @author leadpony
 */
class HtmlElementBlockBuilder extends HtmlBlockBuilder {

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
    
    static HtmlBlockBuilder start(InputSequence input) {
        Matcher m = START_PATTERN.matcher(input);
        if (!m.find()) {
            return null;
        }
        String tag = m.group(3);
        if (tag == null) {
            tag = m.group(6);
        }
        if (ELEMENTS_ALLOWED.contains(tag.toLowerCase())) {
            return new HtmlElementBlockBuilder();
        }
        return null;
    }
    
    @Override
    public Result processLine(InputSequence input) {
        if (input.isBlank()) {
            return Result.COMPLETED;
        }
        accumelateLine(input);
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
class HtmlTagBlockBuilder extends HtmlBlockBuilder {
    
    @SuppressWarnings("serial")
    private static final Set<String> ELEMENTS_NOT_ALLOWED = new HashSet<String>() {{
        add("script");
        add("pre");
        add("style");
    }};
    
    static HtmlBlockBuilder start(InputSequence input) {
        Matcher m = HtmlMatchers.newTagLineMatcher(input);
        if (!m.matches()) {
            return null;
        }
        String tag = extractTagName(m.group());
        if (!ELEMENTS_NOT_ALLOWED.contains(tag.toLowerCase())) {
            return new HtmlTagBlockBuilder();
        }
        return null;
    }
    
    @Override
    public Result processLine(InputSequence input) {
        if (input.isBlank()) {
            return Result.COMPLETED;
        }
        accumelateLine(input);
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
