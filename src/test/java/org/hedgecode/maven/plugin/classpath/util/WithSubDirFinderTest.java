/*
 * Copyright (c) 2015. Developed by HedgeCode.
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

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for {@link WithSubDirFinder}.
 *
 * @author Dmitry Samoshin aka gotty
 */
@RunWith(JUnit4.class)
public class WithSubDirFinderTest extends DirFinderTest {

    protected static final String VALID_FILE_1_MASK = "*1*.ja*";

    @Override
    @Test
    public void testFind() throws Exception {
        FileFinder fileFinder = new WithSubDirFinder(
                new File(getTestDir())
        );

        Set<File> expectedSet = new LinkedHashSet<>();
        for (String file : VALID_FILES) {
            expectedSet.add(new File(getTestDir(), file));
        }

        /* Valid all jars */
        Set<File> testedSet = fileFinder.find(VALID_FILE_MASK);
        assertEquals(expectedSet, testedSet);

        /* Invalid */
        boolean isFileFindException = false;
        try {
            fileFinder.find(INVALID_FILE_MASK);
        } catch (Exception e) {
            isFileFindException = e.getClass().equals(FileFinderException.class);
        }
        assertTrue(isFileFindException);

        /* Valid 1.jar */
        expectedSet = new LinkedHashSet<>();
        expectedSet.add(new File(getTestDir(), VALID_FILES[1]));

        testedSet = fileFinder.find(VALID_FILE_1_MASK);
        assertEquals(expectedSet, testedSet);
    }

}
