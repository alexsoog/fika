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
package io.github.leadpony.fika.publication.project;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

/**
 * @author leadpony
 *
 */
class ProjectParser {
    
    private final Object root;
    private final ProjectParseListener listener;
    
    public ProjectParser(InputStream in, ProjectParseListener listener) {
        this.root = loadYaml(in);
        this.listener = listener;
    }
    
    public void root() {
        if (!(this.root instanceof Map)) {
            // TODO:
        }
        @SuppressWarnings("unchecked")
        Map<String, ?> map = (Map<String, ?>)this.root;
        for (String key: map.keySet()) {
            Object value = map.get(key);
            if (key.equals("pages")) {
                pages(key, value);
            } else {
                rootField(key, value);
            }
        }
    }

    private void rootField(String key, Object value) {
        switch (key) {
        case "title":
            listener.title(valueAsString(key, value));
            break;
        case "description":
            listener.description(valueAsString(key, value));
            break;
        case "url":
            listener.url(valueAsString(key, value));
            break;
        case "copyright":
            listener.copyright(valueAsString(key, value));
            break;
        case "author":
            listener.author(valueAsStringList(key, value));
            break;
        default:
            // TODO:
            break;
        }
    }
    
    private void pages(String key, Object value) {
        listener.enterPages();
        for (Object item: valueAsList(key, value)) {
            navigationItem(item);
        }
        listener.exitPages();
    }
    
    private void navigationItem(Object item) {
        if (item instanceof List) {
            // TODO:
        } else if (item instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, ?> map = (Map<String, ?>)item;
            if (map.size() != 1) {
                // TODO:
            }
            String label = map.keySet().iterator().next();
            Object value = map.get(label);
            if (value instanceof Map) {
                // TODO:
            } else if (value instanceof List) {
                listener.enterNavigationGroup(label);
                for (Object childItem: (List<?>)value) {
                    navigationItem(childItem);
                }
                listener.exitNavigationGroup();
            } else {
                listener.navigationLeaf(label, value.toString());
            }
        } else if (item instanceof String) {
            listener.navigationLeaf(null, item.toString());
        }
    }
    
    //
    
    private static Object loadYaml(InputStream in) {
        Yaml yaml = new Yaml();
        return yaml.load(in);
    }

    private static String valueAsString(String key, Object value) {
        if (value instanceof Map || value instanceof List) {
            // TODO:
            return null;
        }
        return value.toString();
    }

    private static List<String> valueAsStringList(String key, Object value) {
        if (value instanceof Map) {
            // TODO:
            return null;
        }
        List<String> list = new ArrayList<>();
        if (value instanceof List<?>){
            for (Object item: (List<?>)value) {
                list.add((String)item);
            }
        } else {
            list.add(value.toString());
        }
        return list;
    }
    
    private static List<?> valueAsList(String key, Object value) {
        if (!(value instanceof List)) {
            // TODO:
            return null;
        }
        return (List<?>)value;
    }
}
