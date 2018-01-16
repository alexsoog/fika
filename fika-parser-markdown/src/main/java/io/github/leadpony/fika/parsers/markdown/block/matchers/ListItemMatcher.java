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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.leadpony.fika.core.model.Block;
import io.github.leadpony.fika.parsers.markdown.block.BlockType;
import io.github.leadpony.fika.parsers.markdown.block.BlockMatcher;
import io.github.leadpony.fika.parsers.markdown.block.BlockTrait;
import io.github.leadpony.fika.parsers.markdown.block.ContainerBlockMatcher;
import io.github.leadpony.fika.parsers.markdown.block.MatcherMode;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;

/**
 * Matcher for list items.
 * 
 * @author leadpony
 */
abstract class ListItemMatcher extends ContainerBlockMatcher {
    
    private final int indentSize;
    private final boolean empty;
    private boolean loose;
    private int lastBlankLineNo;
    private int linesNotMatched;
    
    protected ListItemMatcher(int indentSize, boolean empty) {
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
    public BlockTrait blockTrait() {
        return BlockType.LIST_ITEM;
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
        Result result = findAndInvokeChildMatcher(contentAfterMarker(input));
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
    public BlockMatcher interrupt(InputSequence input, MatcherMode mode) {
        assert(isInterruptible());
        int indentSize = input.countLeadingSpaces(0, this.indentSize);
        if (indentSize < this.indentSize) {
            return interrupterOfSameType(input);
        }
        return null;
    }
    
    @Override
    protected void openChildMatcher(BlockMatcher childMatcher) {
        if (lineNo() == this.lastBlankLineNo + 1) {
            this.loose = true;
        }
        super.openChildMatcher(childMatcher);
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

    abstract boolean isSameTypeAs(ListItemMatcher other);
    
    abstract BlockMatcher interrupterOfSameType(InputSequence input);
}

/**
 * @author leadpony
 */
class BulletListItemMatcher extends ListItemMatcher {

    private final char bullet;

    private static final String MARKERS = "+-*";

    static BulletListItemMatcher matcher(InputSequence input, int maxIndent) {
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
        return new BulletListItemMatcher(indentSize, c, empty);
    }
    
    private BulletListItemMatcher(int indentSize, char bullet, boolean empty) {
        super(indentSize, empty);
        this.bullet = bullet;
    }
    
    @Override
    boolean isSameTypeAs(ListItemMatcher other) {
        if (other == null || !(other instanceof BulletListItemMatcher)) {
            return false;
        }
        BulletListItemMatcher casted = (BulletListItemMatcher)other;
        return this.bullet == casted.bullet;
    }
    
    @Override
    BlockMatcher interrupterOfSameType(InputSequence input) {
        BulletListItemMatcher m = matcher(input, indentSize() + 3);
        if (m != null && m.bullet == this.bullet) {
            return m;
        }
        return null;
    }
}

/**
 * @author leadpony
 */
class OrderedListItemMatcher extends ListItemMatcher {

    private final int number;
    private final String delimiter;
    
    private static final Pattern MARKER_PATTERN = Pattern.compile("^(\\d{1,9})([.)])");
    
    static OrderedListItemMatcher matcher(InputSequence input, int maxIndent) {
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
        return new OrderedListItemMatcher(indentSize, number, m.group(2), empty);
    }

    private OrderedListItemMatcher(int indentSize, int number, String delimiter, boolean empty) {
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
    boolean isSameTypeAs(ListItemMatcher other) {
        if (other == null || !(other instanceof OrderedListItemMatcher)) {
            return false;
        }
        OrderedListItemMatcher casted = (OrderedListItemMatcher)other;
        return this.delimiter.equals(casted.delimiter);
    }

    @Override
    boolean canInterruptParagraph() {
        return super.canInterruptParagraph() && number() == 1;
    }

    @Override
    BlockMatcher interrupterOfSameType(InputSequence input) {
        OrderedListItemMatcher m = matcher(input, indentSize());
        if (m != null && m.delimiter.equals(this.delimiter)) {
            return m;
        }
        return null;
    }
}
