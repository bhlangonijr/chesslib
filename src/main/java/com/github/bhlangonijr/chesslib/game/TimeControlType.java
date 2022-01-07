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
 * All possible types of time control.
 */
public enum TimeControlType {
    /**
     * An unknown type of time control, used when it is not possible to infer the type of time control.
     */
    UNKNOW,
    /**
     * The time bonus time control type, for example {@code "4500+60"}.
     */
    TIME_BONUS,
    /**
     * The fixed depth time control type, for example {@code "4500"}.
     */
    FIXED_DEPTH,
    /**
     * The time-per-move time control type, for example {@code "*180"}.
     */
    TIME_PER_MOVE,
    /**
     * The moves-per-time time control type, for example {@code "40/9000"}.
     */
    MOVES_PER_TIME,
    /**
     * The nodes time control type.
     */
    NODES

}
