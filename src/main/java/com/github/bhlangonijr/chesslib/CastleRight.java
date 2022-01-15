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
 * The different combinations of castle rights for a side in a chess game. A side can castle only king-side (short
 * castle), only queen-side (long castle), both, or neither king-side nor queen-side.
 */
public enum CastleRight {
    /**
     * The king-side castle right (short castle).
     */
    KING_SIDE,
    /**
     * The queen-side castle right (long castle).
     */
    QUEEN_SIDE,
    /**
     * Both king and queen-side castle right.
     */
    KING_AND_QUEEN_SIDE,
    /**
     * No castle right, i.e. it is no possible to castle.
     */
    NONE;

    /**
     * Returns a castle right given its name.
     * <p>
     * Same as invoking {@link CastleRight#valueOf(String)}.
     *
     * @param v name of the castle right
     * @return the castle right with the specified name
     * @throws IllegalArgumentException if the name does not correspond to any castle right
     */
    public static CastleRight fromValue(String v) {
        return valueOf(v);
    }

    /**
     * Returns the name of the castle right.
     *
     * @return the name of the castle right
     */
    public String value() {
        return name();
    }
}


