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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.github.leadpony.fika.core.nodes.Node;
import io.github.leadpony.fika.core.parser.helper.nodes.Modifiable;
import io.github.leadpony.fika.core.parser.helper.nodes.SimpleHeading;
import io.github.leadpony.fika.core.parser.helper.nodes.SimpleParagraph;

/**
 * @author leadpony
 */
class ParagraphMatcher extends AbstractBlockMatcher {
    
    private final List<String> lines;

    private boolean underlined;
    private int headingLevel;
    
    private static final int PRECEDENCE = 2;
    private static final Pattern UNDERLINE_PATTERN = Pattern.compile("\\u0020{0,3}(=+|-{2,})\\u0020*");
    
    ParagraphMatcher() {
        this.lines = new ArrayList<>();
        this.underlined = false;
        this.headingLevel = 0;
    }
    
    @Override
    public boolean isInterruptible() {
        return true;
    }
    
    @Override
    public BlockMatcher interrupt(Content content) {
        // Handles underline before interrupted by ThematicBreakMatcher. 
        if (testUnderline(content)) {
            return null;
        }
        return super.interrupt(content);
    }
    
    @Override
    public int precedence() {
        return PRECEDENCE;
    }
    
    @Override
    public Node close() {
        Modifiable node = createNode();
        String content = lines.stream().collect(Collectors.joining("\n"));
        context().addInline(node, content.trim());
        return node;
    }
    
    @Override
    protected Status match(Content content, int lineNo) {
        if (lineNo == 0) {
            appendLine(content);
            return Status.CONTINUED;
        } else if (testUnderline(content)) {
            return Status.COMPLETED;
        } else if (content.isBlank()) {
            return Status.COMPLETED;
        }
        appendLine(content);
        return Status.CONTINUED;
    }
    
    private void appendLine(Content content) {
        String extracted = content.trimLeadingSpaces().toOriginalString();
        this.lines.add(extracted);
    }

    private boolean testUnderline(Content content) {
        if (!underlined) {
            underlined = UNDERLINE_PATTERN.matcher(content).matches();
            if (underlined) {
                this.headingLevel = probeHeadingLevel(content);
            }
        }
        return underlined;
    }
    
    private int probeHeadingLevel(Content content) {
        assert(this.underlined);
        int index = content.detectSmallIndent();
        char c = content.charAt(index);
        return (c == '=') ? 1 : 2;
    }

    private Modifiable createNode() {
        if (this.underlined) {
            return new SimpleHeading(this.headingLevel);
        } else {
            return new SimpleParagraph();
        }
    }
}    
