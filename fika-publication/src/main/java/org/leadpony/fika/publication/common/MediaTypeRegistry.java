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
package org.leadpony.fika.publication.common;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author leadpony
 */
public class MediaTypeRegistry {
    
    private static final Logger log = Logger.getLogger(MediaTypeRegistry.class.getName());
    private static final Properties extensions = loadMapping();

    private static final MediaTypeRegistry instance = new MediaTypeRegistry();
    
    public static MediaTypeRegistry get() {
        return instance;
    }
    
    private MediaTypeRegistry() {
    }
    
    public String guessMediaType(Path path) {
        String extension = getExtensionOf(path);
        return extensions.getProperty(extension);
    }
    
    private String getExtensionOf(Path path) {
        String filename = path.getFileName().toString();
        int index = filename.lastIndexOf('.');
        if (index >= 0) {
            return filename.substring(index + 1).trim();
        }
        return null;
    }
    
    private static Properties loadMapping() {
        final String filename = "extensions.properties";
        Properties p = new Properties();
        try (InputStream in = MediaTypeRegistry.class.getResourceAsStream(filename)) {
            if (in != null) {
                p.load(in);
            } else {
                log.severe("Missing file \"" + filename + "\".");
            }
        } catch (IOException e) {
            log.severe("Failed to load file \"" + filename + "\".");
        }
        return p;
    }
}
