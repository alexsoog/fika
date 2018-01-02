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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.leadpony.fika.core.model.Block;

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
    public BlockType blockType() {
        return BasicBlockType.LIST_ITEM;
    }

    @Override
    public Result match(BlockInputSequence content) {
        final boolean isBlank = content.isBlank();
        if (isBlank) {
            this.lastBlankLineNo = lineNo();
        } else if (lineNo() > 1 && !content.hasLeadingSpaces(indentSize)) {
            // Not indented.
            return matchLazyContinuationLine(content);
        }
        Result result = findAndInvokeChildMatcher(contentAfterMarker(content));
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
    public BlockMatcher interrupt(BlockInputSequence content) {
        assert(isInterruptible());
        int indentSize = content.countLeadingSpaces(0, this.indentSize);
        if (indentSize < this.indentSize) {
            return interrupterOfSameType(content);
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
        return nodeFactory().newListItem();
    }
    
    private BlockInputSequence contentAfterMarker(BlockInputSequence content) {
        if (content.isBlank()) {
            return content;
        }
        int skipSize = this.indentSize;
        if (skipSize > content.length()) {
            skipSize = content.length();
        }
        return content.subContent(skipSize);
    }
    
    protected static int countSpacesAfterMarker(BlockInputSequence content, int offset) {
        if (content.length() <= offset) {
            return 1;
        }
        int spaces = content.countLeadingSpaces(offset);
        if (offset + spaces >= content.length()) {
            spaces = 1;
        } else if (spaces > 4) {
            spaces = 1;
        }
        return spaces;
    }

    abstract boolean isSameTypeAs(ListItemMatcher other);
    
    abstract BlockMatcher interrupterOfSameType(BlockInputSequence content);
}

/**
 * @author leadpony
 */
class BulletListItemMatcher extends ListItemMatcher {

    private final char bullet;

    private static final String MARKERS = "+-*";

    static BulletListItemMatcher matcher(BlockInputSequence content, int maxIndent) {
        if (content.isEmpty()) {
            return null;
        }
        int leadingSpaces = content.countLeadingSpaces(0, maxIndent);
        char c = content.charAt(leadingSpaces);
        if (MARKERS.indexOf(c) < 0) {
            return null;
        }
        int trailingSpaces = countSpacesAfterMarker(content, leadingSpaces + 1);
        if (trailingSpaces == 0) {
            return null;
        }
        int indentSize = leadingSpaces + 1 + trailingSpaces;
        boolean empty = indentSize >= content.length();
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
    BlockMatcher interrupterOfSameType(BlockInputSequence content) {
        BulletListItemMatcher m = matcher(content, indentSize() + 3);
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
    
    static OrderedListItemMatcher matcher(BlockInputSequence content, int maxIndent) {
        int leadingSpaces = content.countLeadingSpaces(0, maxIndent);
        Matcher m = MARKER_PATTERN.matcher(content.subContent(leadingSpaces));
        if (!m.find()) {
            return null;
        }
        int length = m.group().length();
        int trailingSpace = countSpacesAfterMarker(content, leadingSpaces + length);
        if (trailingSpace == 0) {
            return null;
        }
        int indentSize = leadingSpaces + length + trailingSpace;
        int number = Integer.parseInt(m.group(1));
        boolean empty = indentSize >= content.length();
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
    BlockMatcher interrupterOfSameType(BlockInputSequence content) {
        OrderedListItemMatcher m = matcher(content, indentSize());
        if (m != null && m.delimiter.equals(this.delimiter)) {
            return m;
        }
        return null;
    }
}
