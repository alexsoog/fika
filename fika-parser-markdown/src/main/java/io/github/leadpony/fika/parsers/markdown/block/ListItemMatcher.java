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

import io.github.leadpony.fika.core.nodes.Node;
import io.github.leadpony.fika.core.parser.support.nodes.SimpleListItem;

/**
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
    
    int indentSize() {
        return indentSize;
    }
    
    boolean startsWithEmpty() {
        return empty;
    }
    
    abstract BlockMatcher matcherOfSameType(Content content);
    
    @Override
    public BlockType blockType() {
        return BasicBlockType.LIST_ITEM;
    }

    @Override
    public Result match(Content content) {
        final boolean isBlank = content.isBlank();
        if (isBlank) {
            this.lastBlankLineNo = lineNo();
        } else if (lineNo() > 1 && !content.hasIndent(indentSize)) {
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
    public BlockMatcher interrupt(Content content) {
        assert(isInterruptible());
        int indentSize = content.countSpaces(0, this.indentSize);
        if (indentSize < this.indentSize) {
            return matcherOfSameType(content);
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
    protected Node buildNode() {
        return new SimpleListItem();
    }
    
    private Content contentAfterMarker(Content content) {
        if (content.isBlank()) {
            return content;
        }
        int skipSize = this.indentSize;
        if (skipSize > content.length()) {
            skipSize = content.length();
        }
        return content.subContent(skipSize);
    }
    
    protected static int countSpacesAfterMarker(Content content, int offset) {
        if (content.length() <= offset) {
            return 1;
        }
        int spaces = content.countSpaces(offset);
        if (offset + spaces >= content.length()) {
            spaces = 1;
        } else if (spaces > 4) {
            spaces = 1;
        }
        return spaces;
    }
}

class BulletListItemMatcher extends ListItemMatcher {

    private final char bullet;

    private static final String MARKERS = "+-*";

    static BulletListItemMatcher matcher(Content content) {
        if (content.isEmpty()) {
            return null;
        }
        int i = content.countSpaces(0, 3);
        char c = content.charAt(i);
        if (MARKERS.indexOf(c) < 0) {
            return null;
        }
        int spaces = countSpacesAfterMarker(content, ++i);
        if (spaces == 0) {
            return null;
        }
        int indentSize = i + spaces;
        boolean empty = indentSize >= content.length();
        return new BulletListItemMatcher(indentSize, c, empty);
    }
    
    private BulletListItemMatcher(int indentSize, char bullet, boolean empty) {
        super(indentSize, empty);
        this.bullet = bullet;
    }
    
    @Override
    BlockMatcher matcherOfSameType(Content content) {
        BulletListItemMatcher m = matcher(content);
        if (m != null && m.bullet == this.bullet) {
            return m;
        }
        return null;
    }
}

class OrderedListItemMatcher extends ListItemMatcher {

    private final int number;
    private final String delimiter;
    
    private static final Pattern MARKER_PATTERN = Pattern.compile("^\\u0020{0,3}(\\d{1,9})([.)])");
    
    static OrderedListItemMatcher matcher(Content content) {
        Matcher m = MARKER_PATTERN.matcher(content);
        if (!m.find()) {
            return null;
        }
        int length = m.group().length();
        int number = Integer.parseInt(m.group(1));
        int spaces = countSpacesAfterMarker(content, length);
        if (spaces == 0) {
            return null;
        }
        int indentSize = length + spaces;
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
    BlockMatcher matcherOfSameType(Content content) {
        OrderedListItemMatcher m = matcher(content);
        if (m != null && m.delimiter.equals(this.delimiter)) {
            return m;
        }
        return null;
    }
}
