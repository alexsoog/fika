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
package io.github.leadpony.fika.core.parser.helper.nodes;

import java.util.List;

import io.github.leadpony.fika.core.nodes.Document;
import io.github.leadpony.fika.core.nodes.Node;

/**
 * @author leadpony
 */
public class SimpleDocument extends CompositeNode implements Document {
    
    public SimpleDocument(List<Node> children) {
        super(children);
    }
}
