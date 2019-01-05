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
package org.leadpony.fika.parser.markdown;

import java.io.StringWriter;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.leadpony.fika.core.core.Parser;
import org.leadpony.fika.core.core.ParserFactory;
import org.leadpony.fika.core.core.ParserService;
import org.leadpony.fika.core.model.Document;
import org.leadpony.fika.core.model.Node;
import org.leadpony.fika.core.DocumentService;
import org.leadpony.fika.core.DocumentWriter;
import org.leadpony.fika.core.DocumentWriterBuilder;

/**
 * @author leadpony
 */
public abstract class AbstractSpecTest {

    private static final ParserService service = ParserService.get("text/markdown");
    private static final ParserFactory factory = service.createParserFactory();

    private static DocumentService htmlService;

    protected AbstractSpecTest() {
    }

    @BeforeAll
    public static void setUpOnce() {
        htmlService = DocumentService.forType("text/html");
    }

    @ParameterizedTest
    @MethodSource("provideFixtures")
    public void test(Fixture fixture) {
        // Assumptions.assumeTrue(fixture.index() == 51);
        Document doc = null;
        try (Parser parser = getParserFactory().createParser(fixture.source())) {
            doc = parser.parse();
        }

        String actual = writeToString(doc);
        String expected = fixture.expected();
        HtmlAssert.assertThat(actual).isEqualTo(expected);
    }

    private String writeToString(Node doc) {
        StringWriter stringWriter = new StringWriter();
        DocumentWriterBuilder builder = htmlService.createWriterBuilder(stringWriter).withFragmentOnly(true);
        try (DocumentWriter writer = builder.build()) {
            writer.write(doc);
        }
        return stringWriter.toString();
    }

    protected ParserFactory getParserFactory() {
        return factory;
    }
}
