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
package io.github.leadpony.fika.parsers.markdown.inline;

import io.github.leadpony.fika.core.model.Node;
import io.github.leadpony.fika.core.model.NodeFactory;

/**
 * Skeletal implementation of {@link InlineHandler}.
 * 
 * @author leadpony
 */
public abstract class AbstractInlineHandler implements InlineHandler {

    private Context context;
    
    @Override
    public void bind(Context context) {
        this.context = context;
    }
    
    protected Context context() {
        return context;
    }
    
    protected NodeFactory nodeFactory() {
        return context.nodeFactory();
    }
    
    protected void appendNode(Node newNode) {
        context.appendNode(newNode);
    }
    
    protected void appendDelimiterRun(DelimiterRun delimiterRun) {
        context.appendDelimiterRun(delimiterRun);
    }
}
