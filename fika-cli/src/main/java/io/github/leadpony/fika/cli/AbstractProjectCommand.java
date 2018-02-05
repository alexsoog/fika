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
package io.github.leadpony.fika.cli;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.github.leadpony.fika.publication.project.Project;

/**
 * @author leadpony
 */
abstract class AbstractProjectCommand implements Command {

    private static final Path DEFAULT_PATH = Paths.get("fika.yml");

    private Path path = DEFAULT_PATH;
    
    @Override
    public void execute() throws Exception {
        execute(loadProject());
    }

    private Project loadProject() throws IOException {
        return Project.loadFrom(this.path);
    }
    
    public abstract void execute(Project project) throws Exception;
}
