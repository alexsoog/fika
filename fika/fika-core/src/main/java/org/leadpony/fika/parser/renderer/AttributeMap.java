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
package org.leadpony.fika.parser.renderer;

import java.util.HashMap;

/**
 * A map of attributes which belongs to an XML element.
 *
 * @author leadpony
 */
@SuppressWarnings("serial")
class AttributeMap extends HashMap<String, String> {

    AttributeMap add(String key, String value) {
        if (value == null) {
            return this;
        }
        put(key, value);
        return this;
    }

    AttributeMap addClass(String value) {
        if (value == null) {
            return this;
        }
        StringBuilder builder = new StringBuilder();
        if (containsKey("class")) {
            builder.append(get("class")).append(" ");
        }
        builder.append(value);
        put("class", builder.toString());
        return this;
    }
}
