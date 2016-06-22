/*
 * Copyright 2016 Ben-Hur Carlos Vieira Langoni Junior
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

public enum GameResult {
    WHITE_WON("1-0"),
    BLACK_WON("0-1"),
    DRAW("1/2-1/2"),
    ONGOING("*");

    static Map<String, GameResult> notation =
            new HashMap<String, GameResult>(4);

    static {
        notation.put("1-0", WHITE_WON);
        notation.put("0-1", BLACK_WON);
        notation.put("1/2-1/2", DRAW);
        notation.put("*", ONGOING);
    }

    String description;

    GameResult(String description) {
        this.description = description;
    }

    public static GameResult fromValue(String v) {
        return valueOf(v);
    }

    public static GameResult fromNotation(String s) {
        return notation.get(s);
    }

    public String getDescription() {
        return description;
    }

    public String value() {
        return name();
    }
}
