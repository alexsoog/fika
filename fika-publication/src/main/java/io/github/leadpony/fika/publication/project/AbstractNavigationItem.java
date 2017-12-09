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
package io.github.leadpony.fika.publication.project;

import java.util.Optional;

abstract class AbstractNavigationItem implements NavigationItem {
    
    private final Optional<String> label;
    private final NavigationGroup group;
    protected final int level;
    
    protected AbstractNavigationItem() {
        this.label = Optional.empty();
        this.group = null;
        this.level = 0;
    }

    protected AbstractNavigationItem(String label, NavigationGroup group) {
        this.label = Optional.ofNullable(label);
        this.group = group;
        this.level = group.level + 1;
    }
    
    @Override
    public Optional<String> label() {
        return label;
    }

    @Override
    public NavigationGroup parentItem() {
        return group;
    }
}