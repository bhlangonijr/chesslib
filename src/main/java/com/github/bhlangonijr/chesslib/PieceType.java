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
 * The enum Piece type.
 */
public enum PieceType {

    /**
     * Pawn piece type.
     */
    PAWN(""),
    /**
     * Knight piece type.
     */
    KNIGHT("N"),
    /**
     * Bishop piece type.
     */
    BISHOP("B"),
    /**
     * Rook piece type.
     */
    ROOK("R"),
    /**
     * Queen piece type.
     */
    QUEEN("Q"),
    /**
     * King piece type.
     */
    KING("K"),
    /**
     * None piece type.
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
     * From value piece type.
     *
     * @param v the v
     * @return the piece type
     */
    public static PieceType fromValue(String v) {
        return valueOf(v);
    }

    /**
     * Returns the {@code PieceType} corresponding to the given short algebraic notation symbol.
     *
     * @param sanSymbol A piece symbol, such as "K", "B" or "".
     * @return the piece type, such as {@code KING}, {@code BISHOP}, or {@code PAWN}.
     * @throws IllegalArgumentException Thrown if the input does not correspond to any standard chess piece type.
     * @since 1.4.0
     */
    public static PieceType fromSanSymbol(String sanSymbol) {
        final PieceType pieceType = sanToType.get(sanSymbol);
        if (pieceType == null) {
            throw new IllegalArgumentException(String.format("Unknown piece '%s'", sanSymbol));
        }
        return pieceType;
    }

    /**
     * Returns the short algebraic notation (SAN) symbol for this piece type.
     * For example, "R" for a rook, "K" for a king, and an empty string for a pawn.
     *
     * @return The short algebraic notation symbol of this piece type.
     * @since 1.4.0
     */
    public String getSanSymbol() {
        return sanSymbol;
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
