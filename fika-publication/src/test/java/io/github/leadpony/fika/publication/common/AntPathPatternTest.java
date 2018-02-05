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

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.assertj.core.api.Assertions.*;

/**
 * @author leadpony
 */
@RunWith(Parameterized.class)
public class AntPathPatternTest {
    
    private final String pattern;
    private final String path;
    private final boolean result;
    
    @Parameters(name = "{0}: {1}")
    public static Iterable<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
            { "*.png", "foo.png", true },
            { "*.png", "foo/bar.png", false },
            { "**/*.png", "foo.png", true },
            { "**/*.png", "foo/bar.png", true },
            { "**/*.png", "foo/bar/baz.png", true },
            { "**/*.png", "foo.gif", false },
            { "images/*.png", "images/foo.png", true },
            { "images/*.png", "foo.png", false },
            { "images/*.png", "foo/bar.png", false }
        });
    }
    
    public AntPathPatternTest(String pattern, String path, boolean result) {
        this.pattern = pattern;
        this.path = path;
        this.result = result;
    }

    @Test
    public void test() {
        Pattern pattern = AntPathPattern.compile(this.pattern);
        Matcher m = pattern.matcher(path);
        boolean result = m.matches();
        assertThat(result).isEqualTo(this.result);
    }
}
