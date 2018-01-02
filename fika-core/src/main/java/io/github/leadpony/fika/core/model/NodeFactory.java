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
package io.github.leadpony.fika.core.model;

/**
 * Factory for creating various kinds of nodes.
 * 
 * @author leadpony
 */
public interface NodeFactory {

    /**
     * Creates a new block quote.
     * 
     * @return newly created node.
     */
    BlockQuote newBlockQuote();
    
    CodeBlock newCodeBlock();

    CodeSpan newCodeSpan();

    Document newDocument();

    Emphasis newEmphasis(int strength);

    Heading newHeading(int level);

    HtmlBlock newHtmlBlock();
    
    HtmlInline newHtmlInline();
    
    Link newLink();

    ListBlock newLiskBlock(ListType type);

    ListItem newListItem();

    Paragraph newParagraph();

    Text newText();
    
    ThematicBreak newThematicBreak();
}
