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
 * The ranks in a board. A <i>rank</i> is a raw in the chessboard, and it is identified as a number from 1 to 8.
 * <p/>
 * Each value defines a single rank, except for the special value {@link Rank#NONE} which represents no rank.
 */
public enum Rank {

    /**
     * The 1st rank.
     */
    RANK_1("1"),
    /**
     * The 2nd rank.
     */
    RANK_2("2"),
    /**
     * The 3rd rank.
     */
    RANK_3("3"),
    /**
     * The 4th rank.
     */
    RANK_4("4"),
    /**
     * The 5th rank.
     */
    RANK_5("5"),
    /**
     * The 6th rank.
     */
    RANK_6("6"),
    /**
     * The 7th rank.
     */
    RANK_7("7"),
    /**
     * The 8th rank.
     */
    RANK_8("8"),
    /**
     * Special value that represents no rank in particular.
     */
    NONE("");

    public static final Rank[] allRanks = values();

    final String notation;

    Rank(String notation) {
        this.notation = notation;
    }

    /**
     * Returns a rank given its name.
     * <p/>
     * Same as invoking {@link Rank#valueOf(String)}.
     *
     * @param v name of the rank
     * @return the rank with the specified name
     * @throws IllegalArgumentException if the name does not correspond to any rank
     */
    public static Rank fromValue(String v) {
        return valueOf(v);
    }

    /**
     * Returns the number that identifies the rank in chess notations.
     *
     * @return the number used to represent the rank, as a string
     */
    public String getNotation() {
        return notation;
    }

    /**
     * Returns the name of the rank.
     *
     * @return the name of the rank
     */
    public String value() {
        return name();
    }
}
