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

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * A utility collection of operations on strings.
 *
 * @author bhlangonijr
 */
public class StringUtil {

    /**
     * Extracts the last char sequence from a string, that is, the last {@code size} chars.
     *
     * @param str  the string
     * @param size the number of chars to extract
     * @return the extracted substring
     */
    public static String lastSequence(final String str, int size) {
        return str.substring(str.length() - size);
    }

    /**
     * Extracts from a string the char sequence of length {@code size} that occurs after the given subsequence.
     *
     * @param str  the string
     * @param seq  the subsequence to search
     * @param size the number of chars to extract
     * @return the extracted substring, or an empty string if the subsequence is not found
     */
    public static String afterSequence(final String str, final String seq, int size) {
        int idx = str.indexOf(seq) + seq.length();
        if (idx == 0) {
            return StringUtils.EMPTY;
        }
        return str.substring(idx, idx + size);
    }

    /**
     * Extracts from a string the char sequence that occurs after the given subsequence.
     *
     * @param str the string
     * @param seq the subsequence to search
     * @return the extracted substring, or an empty string if the subsequence is not found
     */
    public static String afterSequence(final String str, final String seq) {
        int idx = str.indexOf(seq) + seq.length();
        if (idx == 0) {
            return StringUtils.EMPTY;
        }
        return str.substring(idx);
    }

    /**
     * Extracts from a string the char sequence that occurs before the given subsequence.
     *
     * @param str the string
     * @param seq the subsequence to search
     * @return the extracted substring, or the original string if the subsequence is not found
     */
    public static String beforeSequence(final String str, final String seq) {
        int idx = str.indexOf(seq);
        if (idx == -1) {
            return str;
        }
        return str.substring(0, idx);
    }

    /**
     * Removes extra whitespaces in the string. It replaces each sequence of two or more whitespaces with a single one.
     *
     * @param str the string from which to remove extra whitespaces
     * @return the string without extra whitespaces
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
     * Replaces inside a string builder every occurrence of a subsequence of chars with another one. It returns the
     * modified builder.
     *
     * @param builder the builder in which to replace the subsequence of chars
     * @param from    the subsequence to replace with another one
     * @param to      the subsequence with which to replace the original one
     * @return the modified string builder
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
     * Replaces the chars in a builder with the ones stored in the translation table. The indexes in the table refers to
     * the code points of the chars to be replaced.
     *
     * @param str   the string builder in which to replace the chars
     * @param table the translation table
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
     * Replaces the chars in a builder with the ones stored in the translation map.
     *
     * @param str the string builder in which to replace the chars
     * @param map the translation map
     */
    public static void translate(StringBuilder str, Map<Character, Character> map) {
        for (int idx = 0; idx < str.length(); ++idx) {
            char ch = str.charAt(idx);
            Character conversion = map.get(ch);
            if (conversion != null)
                str.setCharAt(idx, conversion);
        }
    }

    /**
     * Counts the occurrences of a char inside a string.
     *
     * @param str         the string in which to count the occurrences of the char
     * @param charToCount the char to count
     * @return the number of times the char is found inside the string
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
