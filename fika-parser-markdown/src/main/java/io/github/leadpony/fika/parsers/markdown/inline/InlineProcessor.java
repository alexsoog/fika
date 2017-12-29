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

import static io.github.leadpony.fika.parsers.markdown.base.Characters.unescape;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.leadpony.fika.core.nodes.Text;

/**
 * @author leadpony
 */
public class InlineProcessor {
    
    private final Pattern DELIMITER_PATTERN = Pattern.compile("[\\*_]+");
    
    public void process(Text text) {
        text.setContent(unescape(text.getContent()));
    }
    
    private DelimiterStack buildDelimiterStack(String content) {
        Matcher m = DELIMITER_PATTERN.matcher(content);
        return null;
    }
}
