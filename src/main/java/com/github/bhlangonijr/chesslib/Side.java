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
 * The enum Side.
 */
public enum Side {

    /**
     * White side.
     */
    WHITE,
    /**
     * Black side.
     */
    BLACK;

    public static Side[] allSides = values();

    /**
     * From value side.
     *
     * @param v the v
     * @return the side
     */
    public static Side fromValue(String v) {
        return valueOf(v);
    }

    /**
     * Value string.
     *
     * @return the string
     */
    public String value() {
        return name();
    }

    /**
     * Flip side.
     *
     * @return the side
     */
    public Side flip() {
        return Side.WHITE.equals(this) ?
                Side.BLACK : Side.WHITE;
    }
}
