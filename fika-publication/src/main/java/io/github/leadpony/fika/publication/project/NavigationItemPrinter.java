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

import java.util.List;

/**
 * @author leadpony
 */
class NavigationItemPrinter {
    
    private final StringBuilder builder = new StringBuilder();
    
    String print(List<NavigationItem> items, int level) {
        printList(items, level);
        return builder.toString();
    }
    
    private void printList(List<NavigationItem> items, int level) {
        final int indent = level * 2;
        for (NavigationItem item: items) {
            indent(indent);
            builder.append("- ");
            item.label().ifPresent(label->builder.append(label).append(": "));
            if (item.pageSource().isPresent()) {
                builder.append(item.pageSource().get().toString()).append("\n");
            } else {
                builder.append("\n");
                printList(item.children(), level + 1);
            }
        }
    }
    
    private void indent(int indent) {
        while (indent-- > 0) {
            builder.append(' ');
        }
    }
}
