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
 * The enum Piece.
 */
public enum Piece {

    /**
     * White pawn piece.
     */
    WHITE_PAWN(Side.WHITE, PieceType.PAWN, "♙", "P"),
    /**
     * White knight piece.
     */
    WHITE_KNIGHT(Side.WHITE, PieceType.KNIGHT, "♘", "N"),
    /**
     * White bishop piece.
     */
    WHITE_BISHOP(Side.WHITE, PieceType.BISHOP, "♗", "B"),
    /**
     * White rook piece.
     */
    WHITE_ROOK(Side.WHITE, PieceType.ROOK, "♖", "R"),
    /**
     * White queen piece.
     */
    WHITE_QUEEN(Side.WHITE, PieceType.QUEEN, "♕", "Q"),
    /**
     * White king piece.
     */
    WHITE_KING(Side.WHITE, PieceType.KING, "♔", "K"),
    /**
     * Black pawn piece.
     */
    BLACK_PAWN(Side.BLACK, PieceType.PAWN, "♟", "p"),
    /**
     * Black knight piece.
     */
    BLACK_KNIGHT(Side.BLACK, PieceType.KNIGHT, "♞", "n"),
    /**
     * Black bishop piece.
     */
    BLACK_BISHOP(Side.BLACK, PieceType.BISHOP, "♝", "b"),
    /**
     * Black rook piece.
     */
    BLACK_ROOK(Side.BLACK, PieceType.ROOK, "♜", "r"),
    /**
     * Black queen piece.
     */
    BLACK_QUEEN(Side.BLACK, PieceType.QUEEN, "♛", "q"),
    /**
     * Black king piece.
     */
    BLACK_KING(Side.BLACK, PieceType.KING, "♚", "k"),
    /**
     * None piece.
     */
    NONE(null, null, "NONE", ".");

    private static final Map<String, Piece> fenToPiece = new HashMap<>(13);
    public static Piece[] allPieces = values();
    private static final Piece[][] pieceMake = {
            {WHITE_PAWN, BLACK_PAWN},
            {WHITE_KNIGHT, BLACK_KNIGHT},
            {WHITE_BISHOP, BLACK_BISHOP},
            {WHITE_ROOK, BLACK_ROOK},
            {WHITE_QUEEN, BLACK_QUEEN},
            {WHITE_KING, BLACK_KING},
            {NONE, NONE},
    };

    static {
        for (final Piece piece : Piece.values()) {
            fenToPiece.put(piece.getFenSymbol(), piece);
        }
    }

    private final Side side;
    private final PieceType type;
    private final String fanSymbol;
    private final String fenSymbol;

    Piece(Side side, PieceType type, String fanSymbol, String fenSymbol) {
        this.side = side;
        this.type = type;
        this.fanSymbol = fanSymbol;
        this.fenSymbol = fenSymbol;
    }

    /**
     * From value piece.
     *
     * @param v the v
     * @return the piece
     */
    public static Piece fromValue(String v) {
        return valueOf(v);
    }

    /**
     * Make piece.
     *
     * @param side the side
     * @param type the type
     * @return the piece
     */
    public static Piece make(Side side, PieceType type) {
        //return Piece.valueOf(side+"_"+type);
        return pieceMake[type.ordinal()][side.ordinal()];
    }

    /**
     * Returns the {@code Piece} corresponding to the given Forsyth-Edwards notation symbol.
     *
     * @param fenSymbol A piece symbol, such as "K", "b" or "p".
     * @return the piece, such as {@code WHITE_KING}, {@code BLACK_BISHOP}, or {@code BLACK_PAWN}.
     * throws IllegalArgumentException Thrown if the input does not correspond to any standard chess piece.
     * @since 1.4.0
     */
    public static Piece fromFenSymbol(String fenSymbol) {
        final Piece piece = fenToPiece.get(fenSymbol);
        if (piece == null) {
            throw new IllegalArgumentException(String.format("Unknown piece '%s'", fenSymbol));
        }
        return piece;
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
     * Gets piece type.
     *
     * @return the piece type
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Gets piece side.
     *
     * @return the piece side
     */
    public Side getPieceSide() {
        return side;
    }

    /**
     * Returns the short algebraic notation (SAN) symbol for this piece type.
     * For example, "R" for a rook, "K" for a king, and an empty string for a pawn.
     *
     * @return The short algebraic notation symbol of this piece type.
     * @since 1.4.0
     */
    public String getSanSymbol() {
        return type.getSanSymbol();
    }

    /**
     * Returns the figurine algebraic notation (FAN) symbol for this piece.
     * For example, "♜" for a black rook, and "♙" for a white pawn.
     *
     * @return The figurine algebraic notation symbol of this piece type.
     * @since 1.4.0
     */
    public String getFanSymbol() {
        return fanSymbol;
    }

    /**
     * Returns the Forsyth-Edwards notation (FEN) symbol for this piece.
     * For example, "r" for a black rook, and "P" for a white pawn.
     *
     * @return The Forsyth-Edwards Notation symbol of this piece.
     * @since 1.4.0
     */
    public String getFenSymbol() {
        return fenSymbol;
    }

}
