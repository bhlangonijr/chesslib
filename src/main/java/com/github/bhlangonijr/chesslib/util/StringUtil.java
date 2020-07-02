/*
 * Copyright 2017 Ben-Hur Carlos Vieira Langoni Junior
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.bhlangonijr.chesslib.util;

import java.util.Map;

/**
 * The type String util.
 *
 * @author bhlangonijr
 */
public class StringUtil {

    /**
     * Extracts the last char sequence of a string
     *
     * @param str  the str
     * @param size the size
     * @return string
     */
    public static String lastSequence(final String str, int size) {
        return str.substring(str.length() - size);
    }

    /**
     * Extracts the sequence after the given subsequence
     *
     * @param str  the str
     * @param seq  the seq
     * @param size the size
     * @return string
     */
    public static String afterSequence(final String str, final String seq, int size) {
        int idx = str.indexOf(seq) + seq.length();
        if (idx == 0) {
            return "";
        }
        return str.substring(idx, idx + size);
    }

    /**
     * Extracts the sequence after the given subsequence
     *
     * @param str the str
     * @param seq the seq
     * @return string
     */
    public static String afterSequence(final String str, final String seq) {
        int idx = str.indexOf(seq) + seq.length();
        if (idx == 0) {
            return "";
        }
        return str.substring(idx);
    }

    /**
     * Extracts the sequence before the given subsequence
     *
     * @param str the str
     * @param seq the seq
     * @return string
     */
    public static String beforeSequence(final String str, final String seq) {
        int idx = str.indexOf(seq);
        if (idx == -1) {
            return str;
        }
        return str.substring(0, idx);
    }

    /**
     * Remove extra-whitespaces in the text
     *
     * @param str the str
     * @return string
     */
    public static String normalize(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (i < str.length() - 1 &&
                    str.charAt(i) == ' ' &&
                    str.charAt(i + 1) == ' ') {
                continue;
            }
            sb.append(str.charAt(i));
        }
        return sb.toString();
    }

    /**
     * Replace all string builder.
     *
     * @param builder the builder
     * @param from    the from
     * @param to      the to
     * @return the string builder
     */
    public static StringBuilder replaceAll(StringBuilder builder, String from, String to) {
        int index = builder.indexOf(from);
        while (index != -1) {
            builder.replace(index, index + from.length(), to);
            index += to.length();
            index = builder.indexOf(from, index);
        }
        return builder;
    }

    /**
     * Translate.
     *
     * @param str   the str
     * @param table the table
     */
    public static void translate(StringBuilder str, char[] table) {
        for (int idx = 0; idx < str.length(); ++idx) {
            char ch = str.charAt(idx);
            if (ch < table.length) {
                ch = table[ch];
                str.setCharAt(idx, ch);
            }
        }
    }

    /**
     * Translate.
     *
     * @param str   the str
     * @param table the table
     */
    public static void translate(StringBuilder str, Map<Character, Character> table) {
        for (int idx = 0; idx < str.length(); ++idx) {
            char ch = str.charAt(idx);
            Character conversion = table.get(ch);
            if (conversion != null)
                str.setCharAt(idx, conversion);
        }
    }

    /**
     * Count occurrences int.
     *
     * @param str         the str
     * @param charToCount the char to count
     * @return the int
     */
    public static int countOccurrences(String str, char charToCount) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == charToCount) {
                count++;
            }
        }
        return count;
    }


}
