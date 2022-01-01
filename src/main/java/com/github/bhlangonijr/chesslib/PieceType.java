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

import java.util.HashMap;
import java.util.Map;

/**
 * All possible piece types in a chess game.
 * <p/>
 * Each value defines a single piece type, except for the special value {@link PieceType#NONE} which represents no type
 * in particular.
 */
public enum PieceType {

    /**
     * The pawn piece type.
     */
    PAWN(""),
    /**
     * The knight piece type.
     */
    KNIGHT("N"),
    /**
     * The bishop piece type.
     */
    BISHOP("B"),
    /**
     * The rook piece type.
     */
    ROOK("R"),
    /**
     * The queen piece type.
     */
    QUEEN("Q"),
    /**
     * The king piece type.
     */
    KING("K"),
    /**
     * Special value that represents no piece type in particular.
     */
    NONE("NONE");

    private static final Map<String, PieceType> sanToType = new HashMap<>(7);

    static {
        for (final PieceType type : PieceType.values()) {
            sanToType.put(type.getSanSymbol(), type);
        }
    }

    private final String sanSymbol;

    PieceType(String sanSymbol) {
        this.sanSymbol = sanSymbol;
    }

    /**
     * Returns a piece type given its name.
     * <p/>
     * Same as invoking {@link PieceType#valueOf(String)}.
     *
     * @param v name of the piece type
     * @return the piece type with the specified name
     * @throws IllegalArgumentException if the name does not correspond to any piece type
     */
    public static PieceType fromValue(String v) {
        return valueOf(v);
    }

    /**
     * Returns the piece type corresponding to the given Short Algebraic Notation (SAN) symbol.
     *
     * @param sanSymbol a piece symbol in SAN notation, such as {@code K} or {@code B}, or the empty string for the pawn
     *                  type
     * @return the piece type that corresponds to the SAN symbol provided in input
     * @throws IllegalArgumentException if the input symbol does not correspond to any standard chess piece type
     */
    public static PieceType fromSanSymbol(String sanSymbol) {
        final PieceType pieceType = sanToType.get(sanSymbol);
        if (pieceType == null) {
            throw new IllegalArgumentException(String.format("Unknown piece '%s'", sanSymbol));
        }
        return pieceType;
    }

    /**
     * Returns the Short Algebraic Notation (SAN) symbol for this piece type. For example, {@code R} for the rook type,
     * {@code K} for the king type, or an empty string for the pawn type.
     *
     * @return the SAN symbol of this piece type
     */
    public String getSanSymbol() {
        return sanSymbol;
    }

    /**
     * Returns the name of the piece type.
     *
     * @return the name of the piece type
     */
    public String value() {
        return name();
    }
}
