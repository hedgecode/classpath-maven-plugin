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
import java.io.FilenameFilter;

/**
 * Mask filter for list of {@link File}.
 *
 * @author Dmitry Samoshin aka gotty
 * @see {@link FilenameFilter}
 */
public class MaskFilter implements FilenameFilter {

    private static final String MASK_SPLIT_REGEX = "[|,;]";

    private static final String BEGIN_REGEX_SYMBOL = "^";
    private static final String ORG_REGEX_SYMBOL = "|";
    private static final String END_REGEX_SYMBOL = "$";

    private static final String[] FROM_SYMBOLS = {".", "?", "*"};
    private static final String[] TO_SYMBOLS = {"\\.", ".", ".*"};

    private String regex;

    public MaskFilter(String mask) {
        this.regex = genRegex(mask);
    }

    @Override
    public boolean accept(File dir, String name) {
        return name.matches(regex);
    }

    private String genRegex(String mask) {
        StringBuilder sb = new StringBuilder();
        String[] patterns = mask.split(MASK_SPLIT_REGEX);
        int counter = 0;
        for (String subPattern : patterns) {
            for (int i = 0; i < FROM_SYMBOLS.length; i++)
                subPattern = subPattern.replace(FROM_SYMBOLS[i], TO_SYMBOLS[i]);
            sb.append(BEGIN_REGEX_SYMBOL).append(subPattern).append(END_REGEX_SYMBOL);
            ++counter;
            if (patterns.length != counter)
                sb.append(ORG_REGEX_SYMBOL);
        }
        return sb.toString();
    }

}
