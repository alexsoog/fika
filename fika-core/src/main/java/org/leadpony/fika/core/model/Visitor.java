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
package org.leadpony.fika.core.model;

/**
 * @author leadpony
 */
public interface Visitor {

    default void visit(Admonition node) {
        visitChildren(node);
    }

    default void visit(BlockQuote node) {
        visitChildren(node);
    }

    default void visit(CodeBlock node) {
        visitChildren(node);
    }

    default void visit(CodeSpan node) {
    }

    default void visit(Document node) {
        visitChildren(node);
    }

    default void visit(Emphasis node) {
        visitChildren(node);
    }
    
    default void visit(HardLineBreak node) {
    }

    default void visit(Heading node) {
        visitChildren(node);
    }

    default void visit(HtmlBlock node) {
    }

    default void visit(HtmlInline node) {
    }

    default void visit(Image node) {
        visitChildren(node);
    }

    default void visit(Link node) {
        visitChildren(node);
    }

    default void visit(ListItem node) {
        visitChildren(node);
    }

    default void visit(OrderedList node) {
        visitChildren(node);
    }

    default void visit(Paragraph node) {
        visitChildren(node);
    }

    default void visit(Text node) {
    }
    
    default void visit(ThematicBreak node) {
    }

    default void visit(UnorderedList node) {
        visitChildren(node);
    }

    default void visitChildren(Node node) {
        for (Node child: node.childNodes()) {
            child.accept(this);
        }
    }
}
