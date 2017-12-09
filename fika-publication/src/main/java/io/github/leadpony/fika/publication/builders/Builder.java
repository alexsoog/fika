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
package io.github.leadpony.fika.publication.builders;

import java.nio.file.Path;

import io.github.leadpony.fika.publication.project.Project;

/**
 * Document builder.
 * 
 * @author leadpony
 */
public interface Builder {

    static final String HTML = "html";
    static final String SINGLE_HTML = "singlehtml";
    
    static Builder newBuilder(String type) {
        if (type == null) {
            throw new NullPointerException("type must not be null.");
        }
        Builder builder = null;
        switch (type) {
        case "html":
            builder = new HtmlBuilder();
            break;
        }
        return builder;
    }

    void setTargetDirectory(Path path);
    
    void setProperty(String name, Object value);
    
    void build(Project project);
}
