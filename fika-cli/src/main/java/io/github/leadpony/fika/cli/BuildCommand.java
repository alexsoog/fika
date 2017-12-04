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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.github.leadpony.fika.core.builders.Builder;

/**
 *
 */
public class BuildCommand extends Command {
    
    private static final String DEFAULT_TYPE = "html";
    
    private String type;
    private Path targetDirectory;
    
    public BuildCommand() {
        this.type = DEFAULT_TYPE;
        this.targetDirectory = Paths.get("target");
    }

    @Override
    protected void run() throws Exception {
        Builder builder = Builder.newBuilder(type);
        if (builder == null) {
            // TODO:
        }
        Files.createDirectories(targetDirectory);
        builder.setTargetDirectory(targetDirectory);
        builder.build(project);
    }
}
