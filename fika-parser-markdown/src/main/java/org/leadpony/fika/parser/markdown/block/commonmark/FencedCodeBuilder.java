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

import static org.leadpony.fika.parser.markdown.common.Characters.SPACE;
import static org.leadpony.fika.parser.markdown.common.Strings.expandReferences;
import static org.leadpony.fika.parser.markdown.common.Strings.unescape;

import org.leadpony.fika.parser.markdown.block.AbstractBlockBuilder;
import org.leadpony.fika.parser.markdown.block.BlockType;
import org.leadpony.fika.parser.markdown.common.InputSequence;
import org.leadpony.fika.parser.model.Block;

class FencedCodeBuilder extends AbstractBlockBuilder {

    private final int indentSize;
    private final char fenceChar;
    private final int fenceLength;
    private final String infoString;

    private final StringBuilder builder;

    FencedCodeBuilder(int indentSize, char fenceChar, int fenceLength, String infoString) {
        this.indentSize = indentSize;
        this.fenceChar = fenceChar;
        this.fenceLength = fenceLength;
        this.infoString = infoString;
        this.builder = new StringBuilder();
    }

    @Override
    public BlockType blockType() {
        return BasicBlockType.FENCED_CODE;
    }

    @Override
    public Result processLine(InputSequence input) {
        if (lineCount() == 0) {
            return Result.CONTINUED;
        }
        if (testClosingFence(input)) {
            return Result.COMPLETED;
        }
        accumulateLine(input);
        return Result.CONTINUED;
    }

    @Override
    protected Block buildBlock() {
        String infoString = unescape(expandReferences(this.infoString));
        String[] words = infoString.split("\\s+");
        String language = words[0];
        return getNodeFactory().createCodeBlock(builder.toString(), language.isEmpty() ? null : language);
    }

    private boolean testClosingFence(InputSequence input) {
        int i = input.countLeadingSpaces(0, 3);
        if (i >= input.length()) {
            return false;
        }
        char c = input.charAt(i);
        if (c != fenceChar) {
            return false;
        }
        int length = 1;
        while (++i < input.length()) {
            c = input.charAt(i);
            if (c == fenceChar) {
                ++length;
            } else if (c == SPACE) {
                break;
            } else {
                return false;
            }
        }
        if (length < fenceLength) {
            return false;
        }
        while (++i < input.length()) {
            c = input.charAt(i);
            if (c != SPACE) {
                return false;
            }
        }
        return true;
    }

    private void accumulateLine(InputSequence input) {
        if (indentSize > 0) {
            int beginIndex = input.countLeadingSpaces(0, indentSize);
            input = input.subSequence(beginIndex);
        }
        builder.append(input.toSourceString()).append('\n');
    }
}
