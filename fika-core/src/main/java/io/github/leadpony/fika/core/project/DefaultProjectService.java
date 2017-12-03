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
package io.github.leadpony.fika.core.project;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import io.github.leadpony.fika.api.project.Project;
import io.github.leadpony.fika.api.project.ProjectService;

/**
 * Default implementation of {@link ProjectService}.
 * 
 * @author leadpony
 */
public class DefaultProjectService extends ProjectService {

    private static final Logger log = Logger.getLogger(DefaultProjectService.class.getName());
   
    @Override
    public Project loadProjectFrom(Path path) throws IOException {
        BasicProjectBuilder builder = new BasicProjectBuilder(path);
        try (InputStream in = Files.newInputStream(path)) {
            ProjectParser parser = new ProjectParser(in, builder);
            parser.root();
        }
        Project project = builder.build();
        log.fine("Project loaded:\n" + project.toString());
        return project;
    }
}
