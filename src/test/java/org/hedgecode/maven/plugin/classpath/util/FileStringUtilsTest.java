/*
 * Copyright (c) 2015. Developed by Hedgecode.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hedgecode.maven.plugin.classpath.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for {@link FileStringUtils}.
 *
 * @author Dmitry Samoshin aka gotty
 */
@RunWith(JUnit4.class)
public class FileStringUtilsTest extends Assert {

    private static final String TEST_EXT = "jar";

    private static final String VALID_EXT_FILE = "/foo/bar/something.JAR";
    private static final String INVALID_EXT_FILE = "/foo/bar/something.txt";

    private static final String TESTED_JAVA_PATH = VALID_EXT_FILE;
    private static final String EXPECTED_JAVA_PATH = "foo.bar";

    private static final String TESTED_REPLACE = "foo/bar/path";
    private static final String EXPECTED_REPLACE = "foo.bar.path";


    @Test
    public void testIsExtType() throws Exception {
        assertTrue(
                FileStringUtils.isExtType(VALID_EXT_FILE, TEST_EXT)
        );
        assertFalse(
                FileStringUtils.isExtType(INVALID_EXT_FILE, TEST_EXT)
        );
    }

    @Test
    public void testGetJavaPath() throws Exception {
        assertEquals(
                EXPECTED_JAVA_PATH, FileStringUtils.getJavaPath(TESTED_JAVA_PATH)
        );
    }

    @Test
    public void testReplacePath() throws Exception {
        assertEquals(
                EXPECTED_REPLACE, FileStringUtils.replacePath(TESTED_REPLACE)
        );
    }

}
