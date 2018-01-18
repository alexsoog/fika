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

import java.io.Writer;
import java.util.EnumSet;
import java.util.Set;

import io.github.leadpony.fika.core.model.Node;

/**
 * HTML renderer.
 * 
 * @author leadpony
 */
public class HtmlRenderer implements Renderer {
    
    private final Set<Option> options;
    
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void render(Node node, Writer writer) {
        HtmlFormatter formatter = createFormatter(writer); 
        HtmlRenderingVisitor visitor = null;
        if (options.contains(Option.HTML_FRAGMENT)) {
            visitor = new HtmlRenderingVisitor(formatter);
        } else {
            visitor = new FullHtmlRenderingVisitor(formatter);
        }
        node.accept(visitor);
    }
    
    protected HtmlRenderer(Builder builder) {
        this.options = builder.options;
    }
    
    protected HtmlFormatter createFormatter(Writer writer) {
        return new MinimalHtmlFormatter(writer);
    }
    
    /**
     * Rendering options.
     * 
     * @author leadpony
     */
    public static enum Option {
        HTML_FRAGMENT
    }

    /**
     * Builder of HTML renderer.
     * 
     * @author leadpony
     */
    public static class Builder {
        
        private final Set<Option> options = EnumSet.noneOf(Option.class);
        
        public Builder withOption(Option option) {
            options.add(option);
            return this;
        }

        public Builder withoutOption(Option option) {
            options.remove(option);
            return this;
        }

        public HtmlRenderer build() {
            return new HtmlRenderer(this);
        }
    }
}
