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
 * All possible squares on a board.
 * <p>
 * Each value defines a single square, except for the special value {@link Square#NONE} which identifies that no
 * square is selected or assigned.
 */
public enum Square {

    /**
     * The {@code A1} square.
     */
    A1,
    /**
     * The {@code B1} square.
     */
    B1,
    /**
     * The {@code C1} square.
     */
    C1,
    /**
     * The {@code D1} square.
     */
    D1,
    /**
     * The {@code E1} square.
     */
    E1,
    /**
     * The {@code F1} square.
     */
    F1,
    /**
     * The {@code G1} square.
     */
    G1,
    /**
     * The {@code H1} square.
     */
    H1,
    /**
     * The {@code A2} square.
     */
    A2,
    /**
     * The {@code B2} square.
     */
    B2,
    /**
     * The {@code C2} square.
     */
    C2,
    /**
     * The {@code D2} square.
     */
    D2,
    /**
     * The {@code E2} square.
     */
    E2,
    /**
     * The {@code F2} square.
     */
    F2,
    /**
     * The {@code G2} square.
     */
    G2,
    /**
     * The {@code H2} square.
     */
    H2,
    /**
     * The {@code A3} square.
     */
    A3,
    /**
     * The {@code B3} square.
     */
    B3,
    /**
     * The {@code C3} square.
     */
    C3,
    /**
     * The {@code D3} square.
     */
    D3,
    /**
     * The {@code E3} square.
     */
    E3,
    /**
     * The {@code F3} square.
     */
    F3,
    /**
     * The {@code G3} square.
     */
    G3,
    /**
     * The {@code H3} square.
     */
    H3,
    /**
     * The {@code A4} square.
     */
    A4,
    /**
     * The {@code B4} square.
     */
    B4,
    /**
     * The {@code C4} square.
     */
    C4,
    /**
     * The {@code D4} square.
     */
    D4,
    /**
     * The {@code E4} square.
     */
    E4,
    /**
     * The {@code F4} square.
     */
    F4,
    /**
     * The {@code G4} square.
     */
    G4,
    /**
     * The {@code H4} square.
     */
    H4,
    /**
     * The {@code A5} square.
     */
    A5,
    /**
     * The {@code B5} square.
     */
    B5,
    /**
     * The {@code C5} square.
     */
    C5,
    /**
     * The {@code D5} square.
     */
    D5,
    /**
     * The {@code E5} square.
     */
    E5,
    /**
     * The {@code F5} square.
     */
    F5,
    /**
     * The {@code G5} square.
     */
    G5,
    /**
     * The {@code H5} square.
     */
    H5,
    /**
     * The {@code A6} square.
     */
    A6,
    /**
     * The {@code B6} square.
     */
    B6,
    /**
     * The {@code C6} square.
     */
    C6,
    /**
     * The {@code D6} square.
     */
    D6,
    /**
     * The {@code E6} square.
     */
    E6,
    /**
     * The {@code F6} square.
     */
    F6,
    /**
     * The {@code G6} square.
     */
    G6,
    /**
     * The {@code H6} square.
     */
    H6,
    /**
     * The {@code A7} square.
     */
    A7,
    /**
     * The {@code B7} square.
     */
    B7,
    /**
     * The {@code C7} square.
     */
    C7,
    /**
     * The {@code D7} square.
     */
    D7,
    /**
     * The {@code E7} square.
     */
    E7,
    /**
     * The {@code F7} square.
     */
    F7,
    /**
     * The {@code G7} square.
     */
    G7,
    /**
     * The {@code H7} square.
     */
    H7,
    /**
     * The {@code A8} square.
     */
    A8,
    /**
     * The {@code B8} square.
     */
    B8,
    /**
     * The {@code C8} square.
     */
    C8,
    /**
     * The {@code D8} square.
     */
    D8,
    /**
     * The {@code E8} square.
     */
    E8,
    /**
     * The {@code F8} square.
     */
    F8,
    /**
     * The {@code G8} square.
     */
    G8,
    /**
     * The {@code H8} square.
     */
    H8,
    /**
     * Special value that represents no square in particular.
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
     * Encodes a rank and a file into a square, returning the square value corresponding to the input values.
     *
     * @param rank a rank in the board
     * @param file a file in the board
     * @return the square that corresponds to the rank and file provided in input
     */
    public static Square encode(Rank rank, File file) {
        return allSquares[rank.ordinal() * 8 + file.ordinal()];
    }

    /**
     * Returns a square given its name.
     * <p>
     * Same as invoking {@link Square#valueOf(String)}.
     *
     * @param v name of the square
     * @return the square with the specified name
     * @throws IllegalArgumentException if the name does not correspond to any square
     */
    public static Square fromValue(String v) {
        return valueOf(v);
    }

    /**
     * Returns the square at position {@code index} in the board, or {@link Square#NONE} if the index is invalid.
     * Valid indexes are included between 0 ({@link Square#A1}) and 63 ({@link Square#H8}), increasing with files and
     * ranks respectively. Thus, index 1 corresponds to {@link Square#B1} and index 8 to {@link Square#A2}.
     *
     * @param index the index of the square
     * @return the corresponding square, if index is valid, otherwise {@link Square#NONE}
     */
    public static Square squareAt(int index) {
        if (index < 0 || index >= allSquares.length) {
            return Square.NONE;
        }
        return allSquares[index];
    }

    /**
     * Returns the squares on the side of the given square. A square on the side is on the same rank and in adjacent
     * files. For instance, side squares of {@link Square#D4} are the two adjacent squares on the 4th rank,
     * {@link Square#C4} and {@link Square#E4}.
     * <p>
     * Note that squares on the edge files ({@code A} and {@code H}) have only one side square instead of two (on files
     * {@code B} and {@code G} respectively).
     *
     * @return the side squares of this square
     */
    public Square[] getSideSquares() {
        return sideSquare.get(this);
    }

    /**
     * Returns the rank of the square.
     *
     * @return the rank of the square
     */
    public Rank getRank() {
        return rankValues[this.ordinal() / 8];
    }

    /**
     * Returns the file of the square.
     *
     * @return the file of the square
     */
    public File getFile() {
        return fileValues[this.ordinal() % 8];
    }

    /**
     * Returns the name of the square.
     *
     * @return the name of the square
     */
    public String value() {
        return name();
    }

    /**
     * Returns the bitboard representation of this square, that is, the single bit in a 64-bits bitmap at the same index
     * of this square. If square is {@link Square#NONE}, an empty bitboard is returned.
     *
     * @return the bitboard representation of this square, as a long value
     */
    public long getBitboard() {
        if (this == NONE) {
            return 0L;
        }
        return bitboard[this.ordinal()];
    }

    /**
     * Returns whether this is a light-square or not (i.e. a dark-square).
     *
     * @return {@code true} if the square is a light-square
     */
    public boolean isLightSquare() {
        return (getBitboard() & Bitboard.lightSquares) != 0L;
    }

}
