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

/**
 * Java based file string utils.
 *
 * @author Dmitry Samoshin aka gotty
 */
public final class FileStringUtils {

    private static final char DOT_SEPARATOR = '.';
    private static final char PATH_SEPARATOR = '/';

    public static boolean isExtType(String fileName, String extType) {
        String ext = getFileExtension(fileName);
        return ext != null && ext.equalsIgnoreCase(extType);
    }

    public static String getJavaPath(String fullFileName) {
        String result = null;
        int i = fullFileName.lastIndexOf(PATH_SEPARATOR);
        if (i >= 0) {
            result = fullFileName.substring(0, i).replace(
                    PATH_SEPARATOR, DOT_SEPARATOR
            );
            while (result.startsWith("" + DOT_SEPARATOR))
                result = result.substring(1);
        }
        return result;
    }

    public static String replacePath(String fullPath) {
        return fullPath.replace(PATH_SEPARATOR, DOT_SEPARATOR);
    }

    private static String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf(DOT_SEPARATOR);
        if (i >= 0)
            return fileName.substring(i + 1);
        return null;
    }

}
