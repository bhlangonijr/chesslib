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
 * The enum Piece.
 */
public enum Piece {

    /**
     * White pawn piece.
     */
    WHITE_PAWN(Side.WHITE, PieceType.PAWN, "♙"),
    /**
     * White knight piece.
     */
    WHITE_KNIGHT(Side.WHITE, PieceType.KNIGHT, "♘"),
    /**
     * White bishop piece.
     */
    WHITE_BISHOP(Side.WHITE, PieceType.BISHOP, "♗"),
    /**
     * White rook piece.
     */
    WHITE_ROOK(Side.WHITE, PieceType.ROOK, "♖"),
    /**
     * White queen piece.
     */
    WHITE_QUEEN(Side.WHITE, PieceType.QUEEN, "♕"),
    /**
     * White king piece.
     */
    WHITE_KING(Side.WHITE, PieceType.KING, "♔"),
    /**
     * Black pawn piece.
     */
    BLACK_PAWN(Side.BLACK, PieceType.PAWN, "♟"),
    /**
     * Black knight piece.
     */
    BLACK_KNIGHT(Side.BLACK, PieceType.KNIGHT, "♞"),
    /**
     * Black bishop piece.
     */
    BLACK_BISHOP(Side.BLACK, PieceType.BISHOP, "♝"),
    /**
     * Black rook piece.
     */
    BLACK_ROOK(Side.BLACK, PieceType.ROOK, "♜"),
    /**
     * Black queen piece.
     */
    BLACK_QUEEN(Side.BLACK, PieceType.QUEEN, "♛"),
    /**
     * Black king piece.
     */
    BLACK_KING(Side.BLACK, PieceType.KING, "♚"),
    /**
     * None piece.
     */
    NONE(null, null, "NONE");

    public static Piece[] allPieces = values();

    private static Piece[][] pieceMake = {
            {WHITE_PAWN, BLACK_PAWN},
            {WHITE_KNIGHT, BLACK_KNIGHT},
            {WHITE_BISHOP, BLACK_BISHOP},
            {WHITE_ROOK, BLACK_ROOK},
            {WHITE_QUEEN, BLACK_QUEEN},
            {WHITE_KING, BLACK_KING},
            {NONE, NONE},
    };

    private final Side side;
    private final PieceType type;
    private String fanSymbol;

    Piece(Side side, PieceType type, String fanSymbol) {
        this.side = side;
        this.type = type;
        this.fanSymbol = fanSymbol;
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
}
