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

package com.github.bhlangonijr.chesslib.game;

import java.util.HashMap;
import java.util.Map;

/**
 * The enum Game result.
 */
public enum GameResult {
    /**
     * White won game result.
     */
    WHITE_WON("1-0"),
    /**
     * Black won game result.
     */
    BLACK_WON("0-1"),
    /**
     * Draw game result.
     */
    DRAW("1/2-1/2"),
    /**
     * Ongoing game result.
     */
    ONGOING("*");

    /**
     * The Notation.
     */
    static Map<String, GameResult> notation =
            new HashMap<String, GameResult>(4);

    static {
        notation.put("1-0", WHITE_WON);
        notation.put("0-1", BLACK_WON);
        notation.put("1/2-1/2", DRAW);
        notation.put("*", ONGOING);
    }

    /**
     * The Description.
     */
    String description;

    GameResult(String description) {
        this.description = description;
    }

    /**
     * From value game result.
     *
     * @param v the v
     * @return the game result
     */
    public static GameResult fromValue(String v) {
        return valueOf(v);
    }

    /**
     * From notation game result.
     *
     * @param s the s
     * @return the game result
     */
    public static GameResult fromNotation(String s) {
        return notation.get(s);
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
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
