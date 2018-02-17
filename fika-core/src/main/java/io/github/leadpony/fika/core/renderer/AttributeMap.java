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
package io.github.leadpony.fika.core.renderer;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A set of attributes that belongs to an XML element.
 * 
 * @author leadpony
 */
public class AttributeMap extends AbstractMap<String, String> {
    
    private final Map<String, String> map = new HashMap<>();
    
    @Override
    public Set<Map.Entry<String, String>> entrySet() {
        return map.entrySet();
    }
    
    public AttributeMap add(String key, String value) {
        if (value == null) {
            return this;
        }
        map.put(key, value);
        return this;
    }

    public AttributeMap addClass(String value) {
        if (value == null) {
            return this;
        }
        StringBuilder builder = new StringBuilder();
        if (map.containsKey("class")) {
            builder.append(map.get("class")).append(" ");
        }
        builder.append(value);
        map.put("class", builder.toString());
        return this;
    }
}
