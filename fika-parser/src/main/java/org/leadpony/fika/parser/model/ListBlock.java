/*
 * Copyright 2017-2019 the Fika authors.
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
package org.leadpony.fika.parser.model;

/**
 * List block.
 * 
 * @author leadpony
 */
public interface ListBlock extends Block {
    
    /**
     * Returns the type of this list.
     * 
     * @return the type of this list. never be {@code null}.
     */
    ListType listType();
    
    /**
     * Checks if this list is tight or not. 
     * 
     * @return {@code true} if this list is tight, {@code false} otherwise. 
     */
    boolean isTight();
    
    /**
     * Checks if this list is loose or not. 
     * 
     * @return {@code true} if this list is loose, {@code false} otherwise. 
     */
    default boolean isLoose() {
        return !isTight();
    }
    
    /**
     * Makes this list tight or loose.
     * 
     * @param tight {@code true} if this list is tight, {@code false} otherwise.
     */
    void setTight(boolean tight);
}
