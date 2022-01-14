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
 * The representation of all possible <i>left-pointing</i> diagonals in a board. A diagonal is identified by two edge
 * squares and defines all the squares of the same color in between, the two edge squares included. A
 * <i>left-pointing</i> diagonal is tilted to the left, that is, the file of the edge square on the greater rank is
 * lower or equal than the file of the other edge square.
 * <p>
 * Note that corners {@code A1} and {@code H8} are special cases of diagonals of length 1.
 */
public enum DiagonalH1A8 {
    /**
     * The {@code A1-A1} diagonal.
     */
    A1_A1,
    /**
     * The {@code B1-A2} diagonal.
     */
    B1_A2,
    /**
     * The {@code C1-A3} diagonal.
     */
    C1_A3,
    /**
     * The {@code D1-A4} diagonal.
     */
    D1_A4,
    /**
     * The {@code E1-A5} diagonal.
     */
    E1_A5,
    /**
     * The {@code F1-A6} diagonal.
     */
    F1_A6,
    /**
     * The {@code G1-A7} diagonal.
     */
    G1_A7,
    /**
     * The {@code H1-A8} diagonal.
     */
    H1_A8,
    /**
     * The {@code B8-H2} diagonal.
     */
    B8_H2,
    /**
     * The {@code C8-H3} diagonal.
     */
    C8_H3,
    /**
     * The {@code D8-H4} diagonal.
     */
    D8_H4,
    /**
     * The {@code E8-H5} diagonal.
     */
    E8_H5,
    /**
     * The {@code F8-H6} diagonal.
     */
    F8_H6,
    /**
     * The {@code G8-H7} diagonal.
     */
    G8_H7,
    /**
     * The {@code H8-H8} diagonal.
     */
    H8_H8
}
