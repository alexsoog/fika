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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author leadpony
 */
public class EntityResolver {

    private static final EntityResolver instance = new EntityResolver();
    private static final String RESOURCE_NAME = "entities.properties";
    
    private final Properties entities = new Properties();

    public static EntityResolver get() {
        return instance;
    }
    
    private EntityResolver() {
        populateEntities();
    }
    
    public String resolve(String s) {
        return entities.getProperty(s);
    }
    
    private void populateEntities() {
        try (InputStream in = getClass().getResourceAsStream(RESOURCE_NAME)) {
            if (in != null) {
                entities.load(in);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
