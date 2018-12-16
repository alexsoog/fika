/*
 * Copyright 2017-2018 the Fika authors.
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
package org.leadpony.fika.parser.markdown.common;

import static org.leadpony.fika.parser.markdown.common.Strings.normalizeString;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author leadpony
 */
public class LinkDefinitionMap extends AbstractMap<String, LinkDefinition> {
    
    private final Map<String, LinkDefinition> definitions = new HashMap<>();
    
    @Override
    public Set<Entry<String, LinkDefinition>> entrySet() {
        return definitions.entrySet();
    }

    @Override
    public LinkDefinition get(Object key) {
        String label = normalizeLabel((String)key);
        return definitions.get(label);
    }
    
    @Override
    public LinkDefinition put(String label, LinkDefinition definition) {
        label = normalizeLabel(label);
        LinkDefinition existing = get(label);
        if (existing != null) {
            return existing;
        }
        return this.definitions.put(label, definition);
    }
    
    @Override
    public String toString() {
        return definitions.toString();
    }
    
    private static String normalizeLabel(String label) {
        String normalized = normalizeString(label);
        return normalized.toLowerCase();
    }
}
