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
 * The representation of all possible <i>right-pointing</i> diagonals in a board. A diagonal is identified by two edge
 * squares and defines all the squares of the same color in between, the two edge squares included. A
 * <i>right-pointing</i> diagonal is tilted to the right, that is, the file of the edge square on the greater rank is
 * greater or equal than the file of the other edge square.
 * <p>
 * Note that corners {@code H1} and {@code A8} are special cases of diagonals of length 1.
 */
public enum DiagonalA1H8 {
    /**
     * The {@code A8-A8} diagonal.
     */
    A8_A8,
    /**
     * The {@code B8-A7} diagonal.
     */
    B8_A7,
    /**
     * The {@code C8-A6} diagonal.
     */
    C8_A6,
    /**
     * The {@code D8-A5} diagonal.
     */
    D8_A5,
    /**
     * The {@code E8-A4} diagonal.
     */
    E8_A4,
    /**
     * The {@code F8-A3} diagonal.
     */
    F8_A3,
    /**
     * The {@code G8-A2} diagonal.
     */
    G8_A2,
    /**
     * The {@code H8-A1} diagonal.
     */
    H8_A1,
    /**
     * The {@code B1-H7} diagonal.
     */
    B1_H7,
    /**
     * The {@code C1-H6} diagonal.
     */
    C1_H6,
    /**
     * The {@code D1-H5} diagonal.
     */
    D1_H5,
    /**
     * The {@code E1-H4} diagonal.
     */
    E1_H4,
    /**
     * The {@code F1-H3} diagonal.
     */
    F1_H3,
    /**
     * The {@code G1-H2} diagonal.
     */
    G1_H2,
    /**
     * The {@code H1-H1} diagonal.
     */
    H1_H1
}
