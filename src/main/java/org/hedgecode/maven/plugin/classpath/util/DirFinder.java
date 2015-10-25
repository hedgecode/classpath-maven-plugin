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
import java.util.regex.Pattern;

/**
 * Search for files on a given name pattern in directory.
 *
 * @author Dmitry Samoshin aka gotty
 */
public class DirFinder implements FileFinder {

    protected static final String WIN_PATH_SEPARATOR = "\\";
    protected static final String PATH_SEPARATOR = "/";
    protected static final Pattern VALID_FILE_PATTERN = Pattern.compile("^[^/*?\"\\\\<>|,;]+$");

    protected File defaultDir;

    public DirFinder() {
        defaultDir = null;
    }

    public DirFinder(File defDir) {
        defaultDir = defDir.isDirectory() ? defDir.getAbsoluteFile() : null;
    }

    @Override
    public Set<File> find(String filePattern) throws FileFinderException {
        File dir = defaultDir;
        filePattern = replaceSeps(filePattern);
        int pathIndex = filePattern.lastIndexOf(PATH_SEPARATOR);
        if (pathIndex != -1) {
            String path = filePattern.substring(0, pathIndex);
            dir = getDir(path, defaultDir);
            filePattern = filePattern.substring(pathIndex+1);
        }

        if (dir == null)
            throw new FileFinderException("Cannot get absolute path from: " + filePattern);

        return findByDir(dir, filePattern);
    }

    protected Set<File> findByDir(File dir, String filePattern) throws FileFinderException {
        Set<File> result = new LinkedHashSet<>();

        if (VALID_FILE_PATTERN.matcher(filePattern).find()) {
            File file = new File(dir, filePattern);
            if (file.isFile())
                result.add(file);
        } else {
            for (String fileName : dir.list(new MaskFilter(filePattern))) {
                File file = new File(dir, fileName);
                if (file.isFile())
                    result.add(file);
            }
        }

        if (result.isEmpty())
            throw new FileFinderException(
                    "Fileset is empty: "
                            + dir.getAbsolutePath() + PATH_SEPARATOR
                            + "[" + filePattern + "]"
            );

        return result;
    }

    protected String replaceSeps(String filePath) {
        return  filePath.replace(WIN_PATH_SEPARATOR, PATH_SEPARATOR);
    }


    public static File getFile(String file, File defDir) throws FileFinderException {
        File result = new File(file);
        if (!result.isFile() && defDir != null)
            result = new File(defDir, file);
        if (!result.isFile())
            throw new FileFinderException("Invalid file: " + file);
        return result.getAbsoluteFile();
    }

    public static File getDir(String dir, File defDir) throws FileFinderException {
        File result = new File(dir);
        if (!result.isDirectory() && defDir != null)
            result = new File(defDir, dir);
        if (!result.isDirectory())
            throw new FileFinderException("Invalid directory: " + dir);
        return result.getAbsoluteFile();
    }


}
