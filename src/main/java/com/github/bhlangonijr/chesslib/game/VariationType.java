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
 * The enum Variation type.
 */
public enum VariationType {
    /**
     * Normal variation type.
     */
    NORMAL,
    /**
     * Chess 960 variation type.
     */
    CHESS960,
    /**
     * Nocastle variation type.
     */
    NOCASTLE,
    /**
     * Wildcastle variation type.
     */
    WILDCASTLE,
    /**
     * Bughouse variation type.
     */
    BUGHOUSE,
    /**
     * Crazyhouse variation type.
     */
    CRAZYHOUSE
}
