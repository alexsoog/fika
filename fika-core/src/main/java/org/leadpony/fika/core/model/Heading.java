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
package org.leadpony.fika.core.model;

/**
 * Heading block.
 * 
 * @author leadpony
 */
public interface Heading extends Block {
    
    /**
     * The minimum level for headings.
     */
    static final int MIN_LEVEL = 1;

    /**
     * The maximum level for headings.
     */
    static final int MAX_LEVEL = 6;

    /**
     * Returns the level of this heading.
     * 
     * @return the level of the heading.
     */
    int getLevel();

    /**
     * Assigns the level of this heading.
     * 
     * @param level the level of the heading to assign.
     * @throws IllegalArgumentException if given {@code level} if out of range.
     */
    void setLevel(int level);

    /**
     * {@inheritDoc}
     */
    @Override
    default void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
