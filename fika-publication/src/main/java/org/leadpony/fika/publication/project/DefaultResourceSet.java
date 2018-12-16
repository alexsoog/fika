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
package org.leadpony.fika.publication.project;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.leadpony.fika.publication.common.AntPathPattern;

/**
 * @author leadpony
 */
class DefaultResourceSet implements ResourceSet {
    
    private final List<Pattern> includes = new ArrayList<>();
    private final List<Pattern> excludes = new ArrayList<>();

    @Override
    public boolean contains(Path path) {
        List<Path> names = new ArrayList<>();
        path.forEach(names::add);
        String joined = names.stream().map(Path::toString).collect(Collectors.joining("/"));
        return contains(joined);
    }
    
    @Override
    public boolean contains(String path) {
        return includes(path) && !excludes(path);
    }
    
    DefaultResourceSet include(String pattern) {
        this.includes.add(AntPathPattern.compile(pattern));
        return this;
    }

    DefaultResourceSet exclude(String pattern) {
        this.excludes.add(AntPathPattern.compile(pattern));
        return this;
    }
    
    private boolean includes(String path) {
        if (this.includes.isEmpty()) {
            return true;
        }
        for (Pattern pattern: this.includes) {
            if (pattern.matcher(path).matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given path should be excluded.
     * 
     * @param path the path to test.
     * @return {@code true} if the path should be excluded.
     */
    private boolean excludes(String path) {
        for (Pattern pattern: this.excludes) {
            if (pattern.matcher(path).matches()) {
                return true;
            }
        }
        return false;
    }
}
