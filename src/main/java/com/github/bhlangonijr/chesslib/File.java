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

package com.github.bhlangonijr.chesslib;

/**
 * The files in a board. A <i>file</i> is a column in the chessboard, and it is identified as a letter from {@code A} to
 * {@code H}.
 * <p/>
 * Each value defines a single file, except for the special value {@link File#NONE} which represents no file.
 */
public enum File {

    /**
     * The {@code A} file.
     */
    FILE_A("A"),
    /**
     * The {@code B} file.
     */
    FILE_B("B"),
    /**
     * The {@code C} file.
     */
    FILE_C("C"),
    /**
     * The {@code D} file.
     */
    FILE_D("D"),
    /**
     * The {@code E} file.
     */
    FILE_E("E"),
    /**
     * The {@code F} file.
     */
    FILE_F("F"),
    /**
     * The {@code G} file.
     */
    FILE_G("G"),
    /**
     * The {@code H} file.
     */
    FILE_H("H"),
    /**
     * Special value that represents no file in particular.
     */
    NONE("");

    public static final File[] allFiles = values();

    final String notation;

    File(String notation) {
        this.notation = notation;
    }

    /**
     * Returns a file given its name.
     * <p/>
     * Same as invoking {@link File#valueOf(String)}.
     *
     * @param v name of the file
     * @return the file with the specified name
     * @throws IllegalArgumentException if the name does not correspond to any file
     */
    public static File fromValue(String v) {
        return valueOf(v);
    }

    /**
     * Returns the letter that identifies the file in chess notations.
     *
     * @return the letter used to represent the file
     */
    public String getNotation() {
        return notation;
    }

    /**
     * Returns the name of the file.
     *
     * @return the name of the file
     */
    public String value() {
        return name();
    }
}
