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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * @author leadpony
 */
class BasicProjectBuilder implements ProjectParseListener {

    private final BasicProject project;
    private final Deque<List<NavigationItem>> childrenStack;
    private NavigationGroup currentGroup;
    
    public BasicProjectBuilder(Path path) {
        this.project = new BasicProject(path);
        this.childrenStack = new ArrayDeque<>();
    }
    
    public Project build() {
        return project;
    }

    @Override
    public void title(String value) {
        project.title = value;
    }

    @Override
    public void description(String value) {
        project.description = value;
    }

    @Override
    public void url(String value) {
        project.url = value;
    }

    @Override
    public void copyright(String value) {
        project.copyright = value;
    }

    @Override
    public void author(List<String> values) {
        project.authors = Collections.unmodifiableList(values);
    }
    
    @Override
    public void enterPages() {
        this.currentGroup = NavigationGroup.root();
        this.childrenStack.push(new ArrayList<>());
    }
    
    @Override
    public void exitPages() {
        project.navigationRoot = finishGroup();
    }

    @Override
    public void enterNavigationGroup(String label) {
        NavigationGroup group = new NavigationGroup(label, currentGroup);
        addNavigationChild(group);
        currentGroup = group;
        childrenStack.push(new ArrayList<>());
    }

    @Override
    public void exitNavigationGroup() {
        finishGroup();
    }
    
    @Override
    public void navigationLeaf(String label, String path) {
        BasicPageSource page = new BasicPageSource(Paths.get(path));
        NavigationLeaf item = new NavigationLeaf(label, page, currentGroup);
        addNavigationChild(item);
    }
    
    private void addNavigationChild(NavigationItem child) {
        List<NavigationItem> children = childrenStack.peek();
        children.add(child);
    }
    
    private NavigationGroup finishGroup() {
        NavigationGroup group = currentGroup;
        List<NavigationItem> children = childrenStack.pop();
        currentGroup.setChildren(children);
        currentGroup = currentGroup.parentItem();
        return group;
    }
}
