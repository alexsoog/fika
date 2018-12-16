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
package org.leadpony.fika.publication.theme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    
    protected AbstractTheme() {
        importResources();
    }

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

    private void importResources() {
        InputStream in = getClass().getResourceAsStream("resources.properties");
        if (in == null) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    String[] values = line.split("\\s*=\\s*");
                    addResource(values[0], values[1]);
                }
            }
        } catch (IOException e) {
            return;
        }
    }
    
    private void addResource(String targetPath, String classPath) {
        if (targetPath.endsWith(".css")) {
            this.stylesheets.add(targetPath);
        } else if (targetPath.endsWith(".js")) {
            this.scripts.add(targetPath);
        }
        this.resources.add(new ClassPathResource(targetPath, classPath, getClass()));
    }
}
