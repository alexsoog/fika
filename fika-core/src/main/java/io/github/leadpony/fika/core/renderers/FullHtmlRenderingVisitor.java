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
package io.github.leadpony.fika.core.renderers;

import java.util.HashMap;
import java.util.Map;

import io.github.leadpony.fika.core.nodes.Document;

/**
 * @author leadpony
 */
public class FullHtmlRenderingVisitor extends HtmlRenderingVisitor {
    
    @SuppressWarnings("serial")
    private static final Map<String, String> charset = new HashMap<String, String>() {{
        put("charset", "UTF-8");
    }};

    FullHtmlRenderingVisitor(HtmlFormatter formatter) {
        super(formatter);
    }

    @Override
    public void visit(Document node) {
        formatter.doctype("html");
        formatter.startTag("html");
        formatter.startTag("head");
        formatter.emptyTag("meta", charset);
        formatter.endTag("head");
        formatter.startTag("body");
        visitChildren(node);
        formatter.endTag("body");
    }
}
