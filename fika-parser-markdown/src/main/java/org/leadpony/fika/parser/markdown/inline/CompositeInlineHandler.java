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
package org.leadpony.fika.parser.markdown.inline;

import java.util.ArrayList;
import java.util.List;

import org.leadpony.fika.parser.markdown.common.InputSequence;

/**
 * @author leadpony
 */
class CompositeInlineHandler implements InlineHandler {
    
    private final List<InlineHandler> handlers = new ArrayList<>();
    
    CompositeInlineHandler(InlineHandler first, InlineHandler second) {
        addHandler(first);
        addHandler(second);
    }
    
    @Override
    public char[] triggerLetters() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public HandlerType handlerType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void bind(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int handleContent(String input, int currentIndex) {
        int consumed = 0;
        for (InlineHandler handler: this.handlers) {
            consumed = handler.handleContent(input, currentIndex);
            if (consumed > 0) {
                return consumed;
            }
        }
        return consumed;
    }

    @Override
    public int handleContent(InputSequence input) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public InlineHandler or(InlineHandler other) {
        addHandler(other);
        return this;
    }
    
    private void addHandler(InlineHandler newHandler) {
        int i = 0;
        for (; i < handlers.size(); ++i) {
            if (handlers.get(i).precedence() >= newHandler.precedence()) {
                break;
            }
        }
        handlers.add(i, newHandler);
    }
}
