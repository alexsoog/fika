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
package org.leadpony.fika.parser.spi.model;

import org.leadpony.fika.parser.model.NodeFactory;

/**
 * Skeletal implementation of a node holding character data.
 *
 * @author leadpony
 */
abstract class AbstractCharSequenceNode extends AbstractNode implements CharSequence {

    private final String content;

    /**
     * Constructs this node.
     *
     * @param factory the factory which created this node.
     * @param content the text content of this node.
     */
    protected AbstractCharSequenceNode(NodeFactory factory, String content) {
        super(factory);
        this.content = content;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int length() {
        return content.length();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public char charAt(int index) {
        return content.charAt(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CharSequence subSequence(int start, int end) {
        return content.subSequence(start, end);
    }

    /**
     * Returns the text content of this node.
     *
     * @return the text content of this node.
     */
    @Override
    public String toString() {
        return content;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String textContent() {
        return content;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void buildTextContent(StringBuilder builder) {
        builder.append(content);
    }
}
