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
package io.github.leadpony.fika.publication.theme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Skeletal implementation of {@link Theme}.
 * 
 * @author leadpony
 */
public abstract class AbstractTheme implements Theme {
    
    private final List<String> stylesheets = new ArrayList<>();
    private final List<String> scripts = new ArrayList<>();
    private final List<Resource> resources = new ArrayList<>();

    @Override
    public List<String> getStylesheets() {
        return Collections.unmodifiableList(stylesheets);
    }

    @Override
    public List<String> getScripts() {
        return Collections.unmodifiableList(scripts);
    }
   
    @Override
    public Iterable<Resource> resources() {
        return Collections.unmodifiableList(resources);
    }
    
    @Override
    public String toString() {
        return name();
    }
    
    protected void css(String path) {
        if (path == null) {
            throw new NullPointerException("path must not be null.");
        }
        this.stylesheets.add(path);
        addResource(path);
    }

    protected void js(String path) {
        if (path == null) {
            throw new NullPointerException("path must not be null.");
        }
        this.scripts.add(path);
        addResource(path);
    }
    
    private void addResource(String path) {
        this.resources.add(new ClassPathResource(path, getClass()));
    }
}
