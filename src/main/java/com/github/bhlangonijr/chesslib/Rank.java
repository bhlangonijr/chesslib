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
 * The enum Rank.
 */
public enum Rank {

    /**
     * Rank 1 rank.
     */
    RANK_1("1"),
    /**
     * Rank 2 rank.
     */
    RANK_2("2"),
    /**
     * Rank 3 rank.
     */
    RANK_3("3"),
    /**
     * Rank 4 rank.
     */
    RANK_4("4"),
    /**
     * Rank 5 rank.
     */
    RANK_5("5"),
    /**
     * Rank 6 rank.
     */
    RANK_6("6"),
    /**
     * Rank 7 rank.
     */
    RANK_7("7"),
    /**
     * Rank 8 rank.
     */
    RANK_8("8"),
    /**
     * None rank.
     */
    NONE("");

    public static Rank[] allRanks = values();
    /**
     * The Notation.
     */
    String notation;

    Rank(String notation) {
        this.notation = notation;
    }

    /**
     * From value rank.
     *
     * @param v the v
     * @return the rank
     */
    public static Rank fromValue(String v) {
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
