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

import static io.github.leadpony.fika.parsers.markdown.base.Characters.trim;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.github.leadpony.fika.core.nodes.Node;
import io.github.leadpony.fika.core.parser.support.nodes.SimpleHeading;
import io.github.leadpony.fika.core.parser.support.nodes.SimpleParagraph;
import io.github.leadpony.fika.core.parser.support.nodes.SimpleText;

/**
 * @author leadpony
 */
class ParagraphMatcher extends AbstractBlockMatcher {
    
    private final List<String> lines;
    private Supplier<Node> nodeSupplier;
    
    private static final Pattern UNDERLINE_PATTERN = Pattern.compile("\\u0020{0,3}(=+|-{2,})\\u0020*");
    
    ParagraphMatcher() {
        this.lines = new ArrayList<>();
        this.nodeSupplier = null;
    }
  
    @Override
    public BlockType blockType() {
        return BasicBlockType.PARAGRAPH;
    }
    
    @Override
    public Result match(Content content) {
        if (lineNo() <= 1) {
            appendLine(content);
            return Result.CONTINUED;
        } else if (content.isBlank() || this.nodeSupplier != null) {
            return Result.COMPLETED;
        }
        appendLine(content);
        return Result.CONTINUED;
    }

    @Override
    public boolean isInterruptible() {
        return lineNo() > 1;
    }
    
    @Override
    public BlockMatcher interrupt(Content content) {
        // Handles underline before interrupted by ThematicBreakMatcher. 
        if (matchHeading(content)) {
            return null;
        }
        return super.interrupt(content);
    }

    @Override
    public Result continueLazily(Content content) {
        if (super.interrupt(content) != null) {
            return Result.NOT_MATCHED;
        } else if (content.isBlank()) {
            return Result.NOT_MATCHED;
        }
        return match(content);
    }
    
    @Override
    protected Node buildNode() {
        Node node = null;
        if (this.nodeSupplier != null) {
            node = this.nodeSupplier.get();
        } else {
            node = new SimpleParagraph(); 
        }
        SimpleText text = new SimpleText(buildContent());
        node.childNodes().add(text);
        context().addInline(text);
        return node;
    }
    
    private void appendLine(Content content) {
        String extracted = content.trimLeadingSpaces().toOriginalString();
        this.lines.add(extracted);
    }

    private boolean matchHeading(Content content) {
        if (!UNDERLINE_PATTERN.matcher(content).matches()) {
            return false;
        }
        int index = content.countSpaces(0, 3);
        char c = content.charAt(index);
        int level = (c == '=') ? 1 : 2;
        this.nodeSupplier = new HeadingSupplier(level);
        return true;
    }

    /**
     * Builds the content of current paragraph.
     * 
     * The paragraph's raw content is formed by
     * concatenating the lines and removing initial and final whitespace.
     * 
     * @return the content of the paragraph.
     */
    private String buildContent() {
        String content = lines.stream().collect(Collectors.joining("\n"));
        return trim(content);    
    }
    
    private static class HeadingSupplier implements Supplier<Node> {
        
        private final int level;
        
        HeadingSupplier(int level) {
            this.level = level;
        }

        @Override
        public Node get() {
            return new SimpleHeading(this.level);
        }
    }
}    
