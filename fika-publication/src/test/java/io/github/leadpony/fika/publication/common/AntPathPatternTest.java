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
package io.github.leadpony.fika.publication.common;

import java.util.regex.Matcher;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.leadpony.fika.publication.common.AntPathPattern;

import static org.assertj.core.api.Assertions.*;

/**
 * @author leadpony
 */
public class AntPathPatternTest {
    
    public static Stream<Arguments> arguments() {
        return Stream.of(
            Arguments.of("*.png", "foo.png", true),
            Arguments.of("*.png", "foo/bar.png", false),
            Arguments.of("**/*.png", "foo.png", true),
            Arguments.of("**/*.png", "foo/bar.png", true),
            Arguments.of("**/*.png", "foo/bar/baz.png", true),
            Arguments.of("**/*.png", "foo.gif", false),
            Arguments.of("images/*.png", "images/foo.png", true),
            Arguments.of("images/*.png", "foo.png", false),
            Arguments.of("images/*.png", "foo/bar.png", false)
        );
    }
    
    @ParameterizedTest(name="[{index}] {0}: {1}")
    @MethodSource("arguments")
    public void test(String pattern, String path, boolean expected) {
        Matcher m = AntPathPattern.compile(pattern).matcher(path);
        boolean result = m.matches();
        assertThat(result).isEqualTo(expected);
    }
}
