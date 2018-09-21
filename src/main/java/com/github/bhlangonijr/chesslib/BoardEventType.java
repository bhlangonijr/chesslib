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
 * Board Event Types
 */
public enum BoardEventType {
    /**
     * On move board event type.
     */
    ON_MOVE,
    /**
     * On undo move board event type.
     */
    ON_UNDO_MOVE,
    /**
     * On load board event type.
     */
    ON_LOAD;

    /**
     * From value board event type.
     *
     * @param v the v
     * @return the board event type
     */
    public static BoardEventType fromValue(String v) {
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
}
