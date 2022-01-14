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

/**
 * The game modes.
 */
public enum Mode {
    /**
     * The Over the Board (OTB) game mode.
     */
    OTB("Over the Board"),
    /**
     * The Internet Chess Server (ICS) game mode.
     */
    ICS("Internet Chess Server");

    /**
     * The description of the game mode.
     */
    final String description;

    Mode(String description) {
        this.description = description;
    }

    /**
     * Returns a game mode given its name.
     * <p>
     * Same as invoking {@link Mode#valueOf(String)}.
     *
     * @param v name of the game mode
     * @return the game mode with the specified name
     * @throws IllegalArgumentException if the name does not correspond to any game mode
     */
    public static Mode fromValue(String v) {
        return valueOf(v);
    }

    /**
     * Returns the name of the game mode.
     *
     * @return the name of the game mode
     */
    public String value() {
        return name();
    }

}
