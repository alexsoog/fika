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
package org.leadpony.fika.parser.markdown.block.matchers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.leadpony.fika.core.model.Block;
import org.leadpony.fika.parser.markdown.block.BlockBuilder;
import org.leadpony.fika.parser.markdown.block.BuilderMode;
import org.leadpony.fika.parser.markdown.block.ContainerBlockBuilder;
import org.leadpony.fika.parser.markdown.block.MatcherType;
import org.leadpony.fika.parser.markdown.common.InputSequence;

/**
 * Builder of list items.
 * 
 * @author leadpony
 */
abstract class ListItemBuilder extends ContainerBlockBuilder {
    
    private final int indentSize;
    private final boolean empty;
    private boolean loose;
    private int lastBlankLineNo;
    private int linesNotMatched;
    
    protected ListItemBuilder(int indentSize, boolean empty) {
        this.indentSize = indentSize;
        this.empty = empty;
        this.loose = false;
        this.lastBlankLineNo = -1;
        this.linesNotMatched = 0;
    }
    
    boolean isLoose() {
        return loose;
    }
    
    /**
     * Returns the indentation size of this matcher.
     * 
     * @return the indentation size.
     */
    int indentSize() {
        return indentSize;
    }
    
    boolean canInterruptParagraph() {
        return !empty;
    }
    
    @Override
    public MatcherType matcherType() {
        return BasicMatcherType.LIST_ITEM;
    }

    @Override
    public Result match(InputSequence input) {
        final boolean isBlank = input.isBlank();
        if (isBlank) {
            this.lastBlankLineNo = lineNo();
        } else if (lineNo() > 1 && !input.hasLeadingSpaces(indentSize)) {
            // Not indented.
            return matchLazyContinuationLine(input);
        }
        Result result = findAndInvokeChildBuilder(contentAfterMarker(input));
        if (result == Result.NOT_MATCHED) {
            if (++linesNotMatched >= 2) {
                return Result.COMPLETED;
            }
        } else {
            linesNotMatched = 0;
        }
        return Result.CONTINUED;
    }
    
    @Override
    public boolean isInterruptible() {
        return lineNo() > 1;
    }

    @Override
    public BlockBuilder interrupt(InputSequence input, BuilderMode mode) {
        assert(isInterruptible());
        int indentSize = input.countLeadingSpaces(0, this.indentSize);
        if (indentSize < this.indentSize) {
            return interrupterOfSameType(input);
        }
        return null;
    }
    
    @Override
    protected void openChildBuilder(BlockBuilder childMatcher) {
        if (lineNo() == this.lastBlankLineNo + 1) {
            this.loose = true;
        }
        super.openChildBuilder(childMatcher);
    }
    
    @Override
    protected Block buildBlock() {
        return getNodeFactory().newListItem();
    }
    
    private InputSequence contentAfterMarker(InputSequence input) {
        if (input.isBlank()) {
            return input;
        }
        int skipSize = this.indentSize;
        if (skipSize > input.length()) {
            skipSize = input.length();
        }
        return input.subSequence(skipSize);
    }
    
    protected static int countSpacesAfterMarker(InputSequence input, int offset) {
        if (input.length() <= offset) {
            return 1;
        }
        int spaces = input.countLeadingSpaces(offset);
        if (offset + spaces >= input.length()) {
            spaces = 1;
        } else if (spaces > 4) {
            spaces = 1;
        }
        return spaces;
    }

    abstract boolean isSameTypeAs(ListItemBuilder other);
    
    abstract BlockBuilder interrupterOfSameType(InputSequence input);
}

/**
 * @author leadpony
 */
class BulletListItemBuilder extends ListItemBuilder {

    private final char bullet;

    private static final String MARKERS = "+-*";

    static BulletListItemBuilder matcher(InputSequence input, int maxIndent) {
        if (input.isEmpty()) {
            return null;
        }
        int leadingSpaces = input.countLeadingSpaces(0, maxIndent);
        char c = input.charAt(leadingSpaces);
        if (MARKERS.indexOf(c) < 0) {
            return null;
        }
        int trailingSpaces = countSpacesAfterMarker(input, leadingSpaces + 1);
        if (trailingSpaces == 0) {
            return null;
        }
        int indentSize = leadingSpaces + 1 + trailingSpaces;
        boolean empty = indentSize >= input.length();
        return new BulletListItemBuilder(indentSize, c, empty);
    }
    
    private BulletListItemBuilder(int indentSize, char bullet, boolean empty) {
        super(indentSize, empty);
        this.bullet = bullet;
    }
    
    @Override
    boolean isSameTypeAs(ListItemBuilder other) {
        if (other == null || !(other instanceof BulletListItemBuilder)) {
            return false;
        }
        BulletListItemBuilder casted = (BulletListItemBuilder)other;
        return this.bullet == casted.bullet;
    }
    
    @Override
    BlockBuilder interrupterOfSameType(InputSequence input) {
        BulletListItemBuilder m = matcher(input, indentSize() + 3);
        if (m != null && m.bullet == this.bullet) {
            return m;
        }
        return null;
    }
}

/**
 * @author leadpony
 */
class OrderedListItemBuilder extends ListItemBuilder {

    private final int number;
    private final String delimiter;
    
    private static final Pattern MARKER_PATTERN = Pattern.compile("^(\\d{1,9})([.)])");
    
    static OrderedListItemBuilder matcher(InputSequence input, int maxIndent) {
        int leadingSpaces = input.countLeadingSpaces(0, maxIndent);
        Matcher m = MARKER_PATTERN.matcher(input.subSequence(leadingSpaces));
        if (!m.find()) {
            return null;
        }
        int length = m.group().length();
        int trailingSpace = countSpacesAfterMarker(input, leadingSpaces + length);
        if (trailingSpace == 0) {
            return null;
        }
        int indentSize = leadingSpaces + length + trailingSpace;
        int number = Integer.parseInt(m.group(1));
        boolean empty = indentSize >= input.length();
        return new OrderedListItemBuilder(indentSize, number, m.group(2), empty);
    }

    private OrderedListItemBuilder(int indentSize, int number, String delimiter, boolean empty) {
        super(indentSize, empty);
        this.number = number;
        this.delimiter = delimiter;
    }
    
    int number() {
        return number;
    }
    
    String delimiter() {
        return delimiter;
    }

    @Override
    boolean isSameTypeAs(ListItemBuilder other) {
        if (other == null || !(other instanceof OrderedListItemBuilder)) {
            return false;
        }
        OrderedListItemBuilder casted = (OrderedListItemBuilder)other;
        return this.delimiter.equals(casted.delimiter);
    }

    @Override
    boolean canInterruptParagraph() {
        return super.canInterruptParagraph() && number() == 1;
    }

    @Override
    BlockBuilder interrupterOfSameType(InputSequence input) {
        OrderedListItemBuilder m = matcher(input, indentSize());
        if (m != null && m.delimiter.equals(this.delimiter)) {
            return m;
        }
        return null;
    }
}
