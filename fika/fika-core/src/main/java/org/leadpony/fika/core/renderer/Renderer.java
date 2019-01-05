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
package org.leadpony.fika.core.renderer;

import java.io.StringWriter;
import java.io.Writer;

import org.leadpony.fika.core.model.Node;

/**
 * A common renderer interface for rendering a node and its descendants.
 *
 * @author leadpony
 */
public interface Renderer {

    /**
     * Renders a node and its descendants and writes the rendered content using the
     * writer.
     *
     * @param node   the starting node to render.
     * @param writer the writer with which this renderer will write the rendered
     *               content.
     * @throws NullPointerException if the specified {@code node} or {@code writer}
     *                              is {@code null}.
     */
    void render(Node node, Writer writer);

    /**
     * Renders a node and its descendants and returns the rendered content as a
     * string.
     *
     * @param node the starting node to render.
     * @return the rendered content.
     * @throws NullPointerException if the specified {@code node} is {@code null}.
     */
    default String render(Node node) {
        StringWriter writer = new StringWriter();
        render(node, writer);
        return writer.toString();
    }
}
