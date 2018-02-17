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
package org.leadpony.fika.core.model;

/**
 * @author leadpony
 */
public interface OrderedList extends ListBlock {

    @Override
    default void accept(Visitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    default ListType listType() {
        return ListType.ORDERED;
    }
    
    /**
     * Returns the starting number of this list.
     * 
     * @return the starting number.
     */
    int getStartNumber();
    
    void setStartNumber(int startNumber);
}
