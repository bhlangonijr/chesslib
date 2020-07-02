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
 * The enum File.
 */
public enum File {

    /**
     * File a file.
     */
    FILE_A("A"),
    /**
     * File b file.
     */
    FILE_B("B"),
    /**
     * File c file.
     */
    FILE_C("C"),
    /**
     * File d file.
     */
    FILE_D("D"),
    /**
     * File e file.
     */
    FILE_E("E"),
    /**
     * File f file.
     */
    FILE_F("F"),
    /**
     * File g file.
     */
    FILE_G("G"),
    /**
     * File h file.
     */
    FILE_H("H"),
    /**
     * None file.
     */
    NONE("");

    public static File[] allFiles = values();
    /**
     * The Notation.
     */
    String notation;

    File(String notation) {
        this.notation = notation;
    }

    /**
     * From value file.
     *
     * @param v the v
     * @return the file
     */
    public static File fromValue(String v) {
        return valueOf(v);
    }

    /**
     * Gets notation.
     *
     * @return the notation
     */
    public String getNotation() {
        return notation;
    }

    /**
     * Value string.
     *
     * @return the string
     */
    public String value() {
        return name();
    }
}
