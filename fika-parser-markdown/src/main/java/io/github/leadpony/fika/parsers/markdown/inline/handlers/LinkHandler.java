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
package io.github.leadpony.fika.parsers.markdown.inline.handlers;

import io.github.leadpony.fika.core.model.Node;
import io.github.leadpony.fika.core.model.Text;
import io.github.leadpony.fika.parsers.markdown.common.InputSequence;
import io.github.leadpony.fika.parsers.markdown.inline.AbstractInlineHandler;
import io.github.leadpony.fika.parsers.markdown.inline.Delimiter;

/**
 * @author leadpony
 */
class LinkHandler extends AbstractInlineHandler {

    public static final String OPENING_CONTENT = "[";
    public static final String CLOSING_CONTENT = "]";

    private static final char TRIGGER_LETTER = '[';
  
    @Override
    public char[] triggerLetters() {
        return new char[] { TRIGGER_LETTER };
    }
    
    @Override
    public int handleContent(InputSequence input) {
        Text text = buildNode(OPENING_CONTENT);
        getAppender().appendNode(text);
        getDelimiterStack().add(new LinkDelimiterRun(text));
        return OPENING_CONTENT.length();
    }

    protected Text buildNode(String content) {
        Text text = getNodeFactory().newText();
        text.setContent(content);
        return text;
    }
    
    class LinkDelimiterRun extends Delimiter {
        
        private String delimiter;

        protected LinkDelimiterRun(Text text) {
            super(text);
            this.delimiter = text.getContent();
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
        public Node makePairWith(Delimiter closer) {
            Node wrapper = buildWrapNode();
            wrapNodes(wrapper, text(), closer.text());
            // Inserts wrapper immediate after the opener.
            text().parentNode().insertChildAfter(wrapper, text());
            return wrapper;
        }
        
        protected Node buildWrapNode() {
            Node node = getNodeFactory().newLink(); 
            return node;
        }
    }
}
