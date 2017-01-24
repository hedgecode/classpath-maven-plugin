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

import java.io.File;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for {@link DirFinder}.
 *
 * @author Dmitry Samoshin aka gotty
 */
@RunWith(JUnit4.class)
public class DirFinderTest extends Assert {

    protected static final String PATH_SEPARATOR = "/";
    protected static final String TEST_RESOURCES_ROOT = PATH_SEPARATOR;
    protected static final String TEST_DIR = "testDir/";

    protected static final String VALID_FILE_MASK = "*.jar";
    protected static final String INVALID_FILE_MASK = "a*.jar";

    protected static final String[] VALID_DIRS = { "", "1", "2", "3" };
    protected static final String[] VALID_FILES = { "0.jar", "1/1.jar", "2/2.jar", "3/3.jar" };

    protected static File resourcesRootDir;

    @Test
    public void testFind() throws Exception {
        for (int i = 0; i < VALID_FILES.length; i++) {
            FileFinder fileFinder = new DirFinder(
                    new File(getTestDir(), VALID_DIRS[i])
            );

            Set<File> expectedSet = new LinkedHashSet<>();
            expectedSet.add(new File(getTestDir(), VALID_FILES[i]));

            /* Valid */
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
        }
    }

    @Test
    public void testGetFile() throws Exception {
        File expectedFile, testedFile;
        for (String file : VALID_FILES) {
            expectedFile = new File(resourcesRootDir, TEST_DIR + file);
            testedFile = DirFinder.getFile(
                    TEST_DIR + file, resourcesRootDir
            );
            assertEquals(expectedFile, testedFile);
        }
    }

    @Test
    public void testGetDir() throws Exception {
        File expectedDir, testedDir;
        for (String dir : VALID_DIRS) {
            expectedDir = new File(resourcesRootDir, TEST_DIR + dir);
            testedDir = DirFinder.getDir(
                    TEST_DIR + dir, resourcesRootDir
            );
            assertEquals(expectedDir, testedDir);
        }
    }

    @BeforeClass
    public static void assignResourcesRoot() throws URISyntaxException {
        resourcesRootDir = new File(
                DirFinderTest.class.getResource(TEST_RESOURCES_ROOT).toURI()
        );
    }

    protected String getTestDir() {
        return resourcesRootDir.getAbsolutePath() + PATH_SEPARATOR + TEST_DIR;
    }

}
