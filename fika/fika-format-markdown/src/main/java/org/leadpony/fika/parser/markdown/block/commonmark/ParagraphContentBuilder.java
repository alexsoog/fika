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

import static org.leadpony.fika.parser.markdown.common.Strings.trimLeadingWhitespace;
import static org.leadpony.fika.parser.markdown.common.Strings.trimWhitespace;

import java.util.ArrayList;
import java.util.List;

import org.leadpony.fika.parser.markdown.common.InputSequence;

/**
 * Builder of content in paragraph.
 * 
 * @author leadpony
 */
class ParagraphContentBuilder {

    private final List<String> lines = new ArrayList<>();
    
    /**
     * Adds a new input to this content.
     * 
     * @param input the input to add.
     */
    public void addLine(InputSequence input) {
        this.lines.add(input.toSourceString());
    }
    
    /**
     * Returns the whole content of the paragraph.
     * 
     * @return the content of the paragraph.
     */
    public String toContent() {
        return toContent(0);
    }
    
    /**
     * Returns the content of the paragraph.
     * 
     * The paragraph's raw content is formed by
     * concatenating the lines and removing initial and final whitespace.
     * 
     * @param linesToSkip the number lines to skip.
     * @return the content of the paragraph.
     */
    public String toContent(int linesToSkip) {
        StringBuilder b = new StringBuilder();
        for (int i = linesToSkip; i < lines.size(); ++i) {
            String line = lines.get(i);
            b.append(trimLeadingWhitespace(line)).append('\n');
        }
        return trimWhitespace(b.toString());
    }
}
