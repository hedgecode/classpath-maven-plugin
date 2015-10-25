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

/**
 * Search for files on a given name pattern with subdirectories.
 *
 * @author Dmitry Samoshin aka gotty
 */
public class WithSubDirFinder extends DirFinder {

    public WithSubDirFinder() {
        super();
    }

    public WithSubDirFinder(File defDir) {
        super(defDir);
    }

    @Override
    protected Set<File> findByDir(File dir, String filePattern) throws FileFinderException {
        Set<File> result = new LinkedHashSet<>();

        Set<File> files;
        try {
            files = super.findByDir(dir, filePattern);
            result.addAll(files);
        } catch (FileFinderException e) {
            // nothing
        }

        for (File subDir : dir.listFiles(new DirFilter())) {
            try {
                files = super.findByDir(subDir, filePattern);
                result.addAll(files);
            } catch (FileFinderException e) {
                // nothing
            }
        }

        if (result.isEmpty())
            throw new FileFinderException(
                    "Fileset with sub dirs is empty: "
                            + dir.getAbsolutePath() + PATH_SEPARATOR
                            + "[" + filePattern + "]"
            );

        return result;
    }
}
