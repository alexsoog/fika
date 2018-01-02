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

import java.io.StringWriter;
import java.io.Writer;
import java.util.EnumSet;
import java.util.Set;

import io.github.leadpony.fika.core.model.Document;

/**
 * @author leadpony
 */
public class HtmlRenderer implements Renderer {
    
    private final Set<Option> options;
    
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String render(Document doc) {
        StringWriter writer = new StringWriter();
        HtmlFormatter formatter = createFormatter(writer); 
        HtmlRenderingVisitor visitor = null;
        if (options.contains(Option.FULL_HTML)) {
            visitor = new FullHtmlRenderingVisitor(formatter);
        } else {
            visitor = new HtmlRenderingVisitor(formatter);
        }
        doc.accept(visitor);
        return writer.toString();
    }
    
    protected HtmlRenderer(Builder builder) {
        this.options = builder.options;
    }
    
    protected HtmlFormatter createFormatter(Writer writer) {
        return new MinimalHtmlFormatter(writer);
    }
    
    public static enum Option {
        FULL_HTML
    }

    public static class Builder {
        
        private final Set<Option> options = EnumSet.of(Option.FULL_HTML);
        
        public HtmlRenderer build() {
            return new HtmlRenderer(this);
        }
        
        public Builder enable(Option option) {
            options.add(option);
            return this;
        }

        public Builder disable(Option option) {
            options.remove(option);
            return this;
        }
    }
}
