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

import java.util.Map;

/**
 * @author leadpony
 *
 */
public interface HtmlFormatter {
    
    void doctype(String type);
    
    void startTag(String tagName);

    void startTag(String tagName, Map<String, Object> attributes);

    void endTag(String tagName);

    void emptyTag(String tagName);

    void emptyTag(String tagName, Map<String, Object> attributes);

    void text(String text);
    
    void preformatted(String text);
}
