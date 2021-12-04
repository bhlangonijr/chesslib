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

import java.util.EnumMap;

/**
 * The enum Square.
 */
public enum Square {
    /**
     * A 1 square.
     */
    A1,
    /**
     * B 1 square.
     */
    B1,
    /**
     * C 1 square.
     */
    C1,
    /**
     * D 1 square.
     */
    D1,
    /**
     * E 1 square.
     */
    E1,
    /**
     * F 1 square.
     */
    F1,
    /**
     * G 1 square.
     */
    G1,
    /**
     * H 1 square.
     */
    H1,
    /**
     * A 2 square.
     */
    A2,
    /**
     * B 2 square.
     */
    B2,
    /**
     * C 2 square.
     */
    C2,
    /**
     * D 2 square.
     */
    D2,
    /**
     * E 2 square.
     */
    E2,
    /**
     * F 2 square.
     */
    F2,
    /**
     * G 2 square.
     */
    G2,
    /**
     * H 2 square.
     */
    H2,
    /**
     * A 3 square.
     */
    A3,
    /**
     * B 3 square.
     */
    B3,
    /**
     * C 3 square.
     */
    C3,
    /**
     * D 3 square.
     */
    D3,
    /**
     * E 3 square.
     */
    E3,
    /**
     * F 3 square.
     */
    F3,
    /**
     * G 3 square.
     */
    G3,
    /**
     * H 3 square.
     */
    H3,
    /**
     * A 4 square.
     */
    A4,
    /**
     * B 4 square.
     */
    B4,
    /**
     * C 4 square.
     */
    C4,
    /**
     * D 4 square.
     */
    D4,
    /**
     * E 4 square.
     */
    E4,
    /**
     * F 4 square.
     */
    F4,
    /**
     * G 4 square.
     */
    G4,
    /**
     * H 4 square.
     */
    H4,
    /**
     * A 5 square.
     */
    A5,
    /**
     * B 5 square.
     */
    B5,
    /**
     * C 5 square.
     */
    C5,
    /**
     * D 5 square.
     */
    D5,
    /**
     * E 5 square.
     */
    E5,
    /**
     * F 5 square.
     */
    F5,
    /**
     * G 5 square.
     */
    G5,
    /**
     * H 5 square.
     */
    H5,
    /**
     * A 6 square.
     */
    A6,
    /**
     * B 6 square.
     */
    B6,
    /**
     * C 6 square.
     */
    C6,
    /**
     * D 6 square.
     */
    D6,
    /**
     * E 6 square.
     */
    E6,
    /**
     * F 6 square.
     */
    F6,
    /**
     * G 6 square.
     */
    G6,
    /**
     * H 6 square.
     */
    H6,
    /**
     * A 7 square.
     */
    A7,
    /**
     * B 7 square.
     */
    B7,
    /**
     * C 7 square.
     */
    C7,
    /**
     * D 7 square.
     */
    D7,
    /**
     * E 7 square.
     */
    E7,
    /**
     * F 7 square.
     */
    F7,
    /**
     * G 7 square.
     */
    G7,
    /**
     * H 7 square.
     */
    H7,
    /**
     * A 8 square.
     */
    A8,
    /**
     * B 8 square.
     */
    B8,
    /**
     * C 8 square.
     */
    C8,
    /**
     * D 8 square.
     */
    D8,
    /**
     * E 8 square.
     */
    E8,
    /**
     * F 8 square.
     */
    F8,
    /**
     * G 8 square.
     */
    G8,
    /**
     * H 8 square.
     */
    H8,
    /**
     * None square.
     */
    NONE;

    private static final Square[] allSquares = Square.values();
    private static final Rank[] rankValues = Rank.values();
    private static final File[] fileValues = File.values();
    private static final long[] bitboard = new long[allSquares.length];

    private static final EnumMap<Square, Square[]> sideSquare =
            new EnumMap<Square, Square[]>(Square.class);

    static {
        for (Square sq : allSquares) {
            bitboard[sq.ordinal()] = 1L << sq.ordinal();
            if (!Square.NONE.equals(sq)) {
                Square[] a = null;
                if (File.FILE_A.equals(sq.getFile())) {
                    a = new Square[1];
                    a[0] = encode(sq.getRank(), File.FILE_B);
                } else if (File.FILE_H.equals(sq.getFile())) {
                    a = new Square[1];
                    a[0] = encode(sq.getRank(), File.FILE_G);
                } else {
                    a = new Square[2];
                    a[0] = encode(sq.getRank(), fileValues[sq.getFile().ordinal() - 1]);
                    a[1] = encode(sq.getRank(), fileValues[sq.getFile().ordinal() + 1]);
                }
                sideSquare.put(sq, a);
            }
        }
    }

    /**
     * encode rank+file to square
     *
     * @param rank the rank
     * @param file the file
     * @return square
     */
    public static Square encode(Rank rank, File file) {
        return allSquares[rank.ordinal() * 8 + file.ordinal()];
    }

    /**
     * From value square.
     *
     * @param v the v
     * @return the square
     */
    public static Square fromValue(String v) {
        return valueOf(v);
    }

    /**
     * Square at square.
     *
     * @param index the index
     * @return the square
     */
    public static Square squareAt(int index) {
        if (index >= allSquares.length) {
            return Square.NONE;
        }
        return allSquares[index];
    }

    /**
     * Get side squares square [ ].
     *
     * @return the square [ ]
     */
    public Square[] getSideSquares() {
        return sideSquare.get(this);
    }

    /**
     * Gets rank.
     *
     * @return the rank
     */
    public Rank getRank() {
        return rankValues[this.ordinal() / 8];
    }

    /**
     * Gets file.
     *
     * @return the file
     */
    public File getFile() {
        return fileValues[this.ordinal() % 8];
    }

    /**
     * Value string.
     *
     * @return the string
     */
    public String value() {
        return name();
    }

    /**
     * Gets bitboard.
     *
     * @return the bitboard
     */
    public long getBitboard() {
        if (this == NONE) {
            return 0L;
        }
        return bitboard[this.ordinal()];
    }

    /**
     * Is light square boolean.
     *
     * @return the boolean
     */
    public boolean isLightSquare() {
        return (getBitboard() & Bitboard.lightSquares) != 0L;
    }

}
