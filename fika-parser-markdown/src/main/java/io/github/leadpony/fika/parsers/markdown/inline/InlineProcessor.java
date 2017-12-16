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
package io.github.leadpony.fika.parsers.markdown.inline;

import static io.github.leadpony.fika.parsers.markdown.inline.Characters.unescape;

import java.util.Arrays;
import java.util.List;

import io.github.leadpony.fika.core.nodes.Inline;
import io.github.leadpony.fika.core.nodes.Text;
import io.github.leadpony.fika.core.parser.helper.nodes.SimpleText;

/**
 * @author leadpony
 */
public class InlineProcessor {
    
    public List<Inline> process(String content) {
        Text text = new SimpleText(unescape(content));
        return Arrays.asList(text);
    }
}
