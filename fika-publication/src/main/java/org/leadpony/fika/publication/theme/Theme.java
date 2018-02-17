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
package org.leadpony.fika.publication.theme;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;

/**
 * @author leadpony
 */
public interface Theme {
    
    /**
     * The name of the default theme.
     */
    static final String DEFAULT_THEME = "cinnamon";
    
    /**
     * Finds the theme by name.
     * 
     * @param name the name of the theme to find.
     * @return found theme.
     * @throws NoSuchElementException if no such theme was found.
     */
    static Theme find(String name) {
        if (name == null) {
            throw new NullPointerException("name must not be null.");
        }
        ServiceLoader<Theme> loader = ServiceLoader.load(Theme.class);
        for (Theme theme: loader) {
            if (name.equals(theme.name())) {
                return theme;
            }
        }
        throw new NoSuchElementException("Specified theme was not found: " + name);
    }
    
    /**
     * Returns the name of this theme.
     * 
     * @return the name of this theme.
     */
    String name();
    
    List<String> getStylesheets();

    List<String> getScripts();
    
    Iterable<Resource> resources();
}
