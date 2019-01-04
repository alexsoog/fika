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
 * Defines type of {@link ListBlock}.
 *
 * @author leadpony
 * @see ListBlock
 */
public enum ListType {
    /**
     * The list contains ordered items.
     */
    ORDERED,
    /**
     * The list contains unordered items.
     */
    UNORDERED,
    /**
     * The list contains terms and definitions.
     */
    DEFINITION
}
