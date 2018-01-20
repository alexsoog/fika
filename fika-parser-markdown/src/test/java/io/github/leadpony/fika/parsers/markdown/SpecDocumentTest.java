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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import io.github.leadpony.fika.core.model.Document;
import io.github.leadpony.fika.core.parser.MarkupLanguage;
import io.github.leadpony.fika.core.parser.Parser;
import io.github.leadpony.fika.core.parser.ParserFactory;
import io.github.leadpony.fika.core.renderers.HtmlRenderer;

/**
 * @author leadpony
 */
public class SpecDocumentTest {

    private static final ParserFactory factory = 
            ParserFactory.newInstance(MarkupLanguage.MARKDOWN);
    
    private static final Path OUTPUT_PATH = Paths.get("target", "spec.html");
    private InputStream resource;
    
    @Before
    public void setUp() {
        resource = getClass().getResourceAsStream("/spec.txt");
        Assume.assumeTrue(resource != null);
    }

    @Test
    public void test() {
        Document doc = null;
        try (Reader reader = new InputStreamReader(resource)) {
            Parser parser = factory.newParser(reader);
            doc = parser.parse();
        } catch (IOException e) {
        }
        
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        try (Writer writer = Files.newBufferedWriter(OUTPUT_PATH)) {
            renderer.render(doc, writer);
        } catch (IOException e) {
        }
    }
}
