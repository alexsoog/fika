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

import static io.github.leadpony.fika.parsers.markdown.common.Strings.trimWhitespace;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.github.leadpony.fika.core.model.Block;
import io.github.leadpony.fika.core.model.Paragraph;
import io.github.leadpony.fika.core.model.Text;
import io.github.leadpony.fika.parsers.markdown.block.AbstractBlockMatcher;
import io.github.leadpony.fika.parsers.markdown.block.BasicBlockType;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcher;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcherFactory;
import io.github.leadpony.fika.parsers.markdown.block.BlockType;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;
import io.github.leadpony.fika.parsers.markdown.common.LinkDefinition;
import io.github.leadpony.fika.parsers.markdown.common.LinkDefinitionMap;
import io.github.leadpony.fika.parsers.markdown.common.Links;

/**
 * @author leadpony
 *
 */
class LinkDefinitionMatcher extends AbstractBlockMatcher {
    
    private final List<String> lines = new ArrayList<>();
    private static final Pattern LINK_DEFINITION_PATTERN = Pattern.compile(Links.LINK_DEFINITION);
   
    @Override
    public BlockType blockType() {
        return BasicBlockType.LINK_DEFINITION;
    }

    @Override
    public boolean isInterruptible() {
        return lineNo() > 1;
    }
   
    @Override
    public Result match(InputSequence input) {
        if (lineNo() <= 1 || !input.isBlank()) {
            appendLine(input);
            return Result.CONTINUED;
        } else {
            return Result.COMPLETED;
        }
    }
    
    @Override
    protected Block buildBlock() {
        String content = lines.stream().collect(Collectors.joining("\n"));
        content = processLinkDefinitions(content);
        return buildParagraph(content);
    }

    private void appendLine(InputSequence input) {
        this.lines.add(input.toSourceString());
    }
    
    private String processLinkDefinitions(String content) {
        LinkDefinitionMap map = context().getLinkDefinitionMap();
        Matcher m = LINK_DEFINITION_PATTERN.matcher(content);
        int end = 0;
        while (m.lookingAt()) {
            String label = m.group("label");
            String destination = m.group("destination");
            String title = m.group("title");
            map.put(label, new LinkDefinition(destination, title));
            end = m.end();
            m.region(end, content.length());
        }
        if (end > 0) {
            content = content.substring(end);
        }
        return content;
    }
    
    private Paragraph buildParagraph(String content) {
        content = trimWhitespace(content);
        if (content.isEmpty()) {
            return null;
        }
        Text text = getNodeFactory().newText();
        text.setContent(content);
        context().addInline(text);
        Paragraph p = getNodeFactory().newParagraph();
        p.appendChild(text);
        return p;
    }
}

class LinkDefinitionMatcherFactory implements BlockMatcherFactory {

    @Override
    public BlockType blockType() {
        return BasicBlockType.LINK_DEFINITION;
    }

    @Override
    public BlockMatcher newMatcher(InputSequence input) {
        int spaces = input.countLeadingSpaces(0,  3);
        if (input.length() > spaces && input.charAt(spaces) == '[') {
            return new LinkDefinitionMatcher();
        } else {
            return null;
        }
    }
}