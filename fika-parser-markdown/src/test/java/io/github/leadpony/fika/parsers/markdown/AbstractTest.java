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
package io.github.leadpony.fika.parsers.markdown;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import io.github.leadpony.fika.core.nodes.Document;
import io.github.leadpony.fika.core.parser.Parser;
import io.github.leadpony.fika.core.renderers.HtmlRenderer;
import io.github.leadpony.fika.parsers.markdown.parser.MarkdownParserFactory;

/**
 * @author leadpony
 *
 */
public abstract class AbstractTest {
    
    private static final MarkdownParserFactory factory = new MarkdownParserFactory();

    protected final Fixture fixture;
    
    protected AbstractTest(String source, String html) {
        this.fixture = new Fixture(source, html);
    }
    
    @Test
    public void test() {
        Parser parser = factory.newParser(fixture.source());
        Document doc = parser.parse();
        HtmlRenderer renderer = HtmlRenderer.builder()
                .disable(HtmlRenderer.Option.FULL_HTML)
                .build();
        String actual = renderer.render(doc);
        String expected = fixture.expected();
        assertThat(wrapXml(actual)).isXmlEqualTo(wrapXml(expected));
    }
    
    private static String wrapXml(String xml) {
        StringBuilder b = new StringBuilder();
        b.append("<_>").append(xml).append("</_>");
        return b.toString();
    }
}
