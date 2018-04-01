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

package com.github.bhlangonijr.chesslib;

import java.util.EnumMap;

public enum Square {
    A1, B1, C1, D1, E1, F1, G1, H1,
    A2, B2, C2, D2, E2, F2, G2, H2,
    A3, B3, C3, D3, E3, F3, G3, H3,
    A4, B4, C4, D4, E4, F4, G4, H4,
    A5, B5, C5, D5, E5, F5, G5, H5,
    A6, B6, C6, D6, E6, F6, G6, H6,
    A7, B7, C7, D7, E7, F7, G7, H7,
    A8, B8, C8, D8, E8, F8, G8, H8,
    NONE;

    private static final EnumMap<Square, Square[]> sideSquare =
            new EnumMap<Square, Square[]>(Square.class);

    static {
        for (Square sq : Square.values()) {
            if (!Square.NONE.equals(sq)) {
                Square a[] = null;
                if (File.FILE_A.equals(sq.getFile())) {
                    a = new Square[1];
                    a[0] = encode(sq.getRank(), File.FILE_B);
                } else if (File.FILE_H.equals(sq.getFile())) {
                    a = new Square[1];
                    a[0] = encode(sq.getRank(), File.FILE_G);
                } else {
                    a = new Square[2];
                    a[0] = encode(sq.getRank(), File.values()[sq.getFile().ordinal() - 1]);
                    a[1] = encode(sq.getRank(), File.values()[sq.getFile().ordinal() + 1]);
                }
                sideSquare.put(sq, a);
            }
        }
    }

    /**
     * encode rank+file to square
     *
     * @param rank
     * @param file
     * @return
     */
    public static Square encode(Rank rank, File file) {
        return Square.values()[rank.ordinal() * 8 + file.ordinal()];
    }

    public static Square fromValue(String v) {
        return valueOf(v);
    }

    public static Square squareAt(int index) {
        if (index >= Square.values().length) {
            return Square.NONE;
        }
        return Square.values()[index];
    }

    public Square[] getSideSquares() {
        return sideSquare.get(this);
    }

    public Rank getRank() {
        return Rank.values()[this.ordinal() / 8];
    }

    public File getFile() {
        return File.values()[this.ordinal() % 8];
    }

    public String value() {
        return name();
    }

    public long getBitboard() {
        if (this == NONE) {
            return 0L;
        }
        return Bitboard.getBbtable(this);
    }

    public boolean isLightSquare() {
        return (getBitboard() & Bitboard.lightSquares) != 0L;
    }

}
