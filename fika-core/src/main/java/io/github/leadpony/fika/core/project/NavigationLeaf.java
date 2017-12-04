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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

class NavigationLeaf extends AbstractNavigationItem {

    private final Optional<PageSource> source;
    
    NavigationLeaf(String label, PageSource source, NavigationGroup group) {
        super(label, group);
        this.source = Optional.of(source);
    }

    @Override
    public List<NavigationItem> children() {
        return Collections.emptyList();
    }
    
    @Override
    public Optional<PageSource> pageSource() {
        return source;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        label().ifPresent(label->b.append(label).append(": "));
        return b.append(pageSource().get()).toString();
    }
}