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
package io.github.leadpony.fika.publication.view;

import io.github.leadpony.fika.core.model.Document;
import io.github.leadpony.fika.core.renderers.HtmlRenderer;

/**
 * @author leadpony
 */
public abstract class AbstractTemplateView implements View {

    private final HtmlRenderer contentRenderer;
    
    protected AbstractTemplateView() {
        this.contentRenderer = buildContentRenderer();
    }
    
    protected String renderContent(Document doc) {
        return this.contentRenderer.render(doc);
    }

    protected HtmlRenderer buildContentRenderer() {
        HtmlRenderer.Builder builder = HtmlRenderer.builder();
        builder.withOption(HtmlRenderer.Option.HTML_FRAGMENT);
        return builder.build();
    }
}
