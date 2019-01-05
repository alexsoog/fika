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
package org.leadpony.fika.format.markdown.inline.commonmark;

import org.leadpony.fika.format.markdown.common.LinkDefinition;
import org.leadpony.fika.format.markdown.inline.AbstractInlineHandler;
import org.leadpony.fika.format.markdown.inline.HandlerType;
import org.leadpony.fika.core.model.Node;
import org.leadpony.fika.core.model.Text;

/**
 * @author leadpony
 */
public class LinkHandler extends AbstractInlineHandler {

    public static final String OPENING_CONTENT = "[";

    private static final char TRIGGER_LETTER = '[';

    @Override
    public char[] triggerLetters() {
        return new char[] { TRIGGER_LETTER };
    }

    @Override
    public HandlerType handlerType() {
        return BasicHandlerType.LINK;
    }

    @Override
    public int handleContent(String input, int currentIndex) {
        Text text = buildNode(OPENING_CONTENT);
        getAppender().appendNode(text);
        getDelimiterStack().add(new LinkDelimiter(text, currentIndex));
        return OPENING_CONTENT.length();
    }

    protected Text buildNode(String content) {
        return getNodeFactory().createText(content);
    }

    class LinkDelimiter extends AbstractLinkDelimiter {

        LinkDelimiter(Text text, int position) {
            super(text, position);
        }

        @Override
        protected Node buildWrapNode(LinkDefinition definition) {
            return getNodeFactory().createLink(definition.destination(), definition.title());
        }
    }
}
