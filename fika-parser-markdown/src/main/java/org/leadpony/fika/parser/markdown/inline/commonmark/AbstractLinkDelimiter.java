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
package org.leadpony.fika.parser.markdown.inline.commonmark;

import org.leadpony.fika.core.model.Node;
import org.leadpony.fika.core.model.Text;
import org.leadpony.fika.parser.markdown.common.LinkDefinition;
import org.leadpony.fika.parser.markdown.inline.Delimiter;

/**
 * Opening delimiter for links and images.
 * 
 * @author leadpony
 */
abstract class AbstractLinkDelimiter extends Delimiter {

    /**
     * The symbol of closing delimiter.
     */
    public static final String CLOSING_CONTENT = "]";
    
    private final String delimiter;
    private final int position;

    AbstractLinkDelimiter(Text text, int position) {
        super(text);
        this.delimiter = text.getContent();
        this.position = position;
    }

    @Override
    public String delimiter() {
        return delimiter;
    }

    @Override
    public boolean canBeOpener() {
        return true;
    }
    
    @Override
    public boolean canBeCloser() {
        return false;
    }
   
    @Override
    public boolean isSameTypeAs(Delimiter other) {
        String delimiter = other.delimiter();
        return delimiter.equals(delimiter()) || delimiter.equals(CLOSING_CONTENT); 
    }

    @Override
    public Node makePairWith(Delimiter closer, Object... params) {
        if (params.length == 0 || !(params[0] instanceof LinkDefinition)) {
            throw new IllegalArgumentException();
        }
        Node wrapper = buildWrapNode((LinkDefinition)params[0]);
        wrapNodes(wrapper, text(), closer.text());
        // Inserts wrapper immediate after the opener.
        text().parentNode().insertChildAfter(wrapper, text());
        return wrapper;
    }
    
    public int getPosition() {
        return position;
    }
    
    /**
     * Builds a new wrapping node.
     * 
     * @param definition the link definition.
     * @return newly created node.
     */
    protected abstract Node buildWrapNode(LinkDefinition definition);
}
