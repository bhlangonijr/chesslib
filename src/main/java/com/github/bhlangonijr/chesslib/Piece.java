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
 * The enum Piece.
 */
public enum Piece {

    /**
     * White pawn piece.
     */
    WHITE_PAWN, /**
     * White knight piece.
     */
    WHITE_KNIGHT, /**
     * White bishop piece.
     */
    WHITE_BISHOP, /**
     * White rook piece.
     */
    WHITE_ROOK, /**
     * White queen piece.
     */
    WHITE_QUEEN, /**
     * White king piece.
     */
    WHITE_KING,
    /**
     * Black pawn piece.
     */
    BLACK_PAWN, /**
     * Black knight piece.
     */
    BLACK_KNIGHT, /**
     * Black bishop piece.
     */
    BLACK_BISHOP, /**
     * Black rook piece.
     */
    BLACK_ROOK, /**
     * Black queen piece.
     */
    BLACK_QUEEN, /**
     * Black king piece.
     */
    BLACK_KING, /**
     * None piece.
     */
    NONE;

    /**
     * The Piece type.
     */
    static EnumMap<Piece, PieceType> pieceType =
            new EnumMap<Piece, PieceType>(Piece.class);
    /**
     * The Piece side.
     */
    static EnumMap<Piece, Side> pieceSide =
            new EnumMap<Piece, Side>(Piece.class);
    private static Piece pieceMake[][] = {
            {WHITE_PAWN, BLACK_PAWN},
            {WHITE_KNIGHT, BLACK_KNIGHT},
            {WHITE_BISHOP, BLACK_BISHOP},
            {WHITE_ROOK, BLACK_ROOK},
            {WHITE_QUEEN, BLACK_QUEEN},
            {WHITE_KING, BLACK_KING},
            {NONE, NONE},
    };

    static {
        pieceType.put(Piece.WHITE_PAWN, PieceType.PAWN);
        pieceType.put(Piece.WHITE_KNIGHT, PieceType.KNIGHT);
        pieceType.put(Piece.WHITE_BISHOP, PieceType.BISHOP);
        pieceType.put(Piece.WHITE_ROOK, PieceType.ROOK);
        pieceType.put(Piece.WHITE_QUEEN, PieceType.QUEEN);
        pieceType.put(Piece.WHITE_KING, PieceType.KING);

        pieceType.put(Piece.BLACK_PAWN, PieceType.PAWN);
        pieceType.put(Piece.BLACK_KNIGHT, PieceType.KNIGHT);
        pieceType.put(Piece.BLACK_BISHOP, PieceType.BISHOP);
        pieceType.put(Piece.BLACK_ROOK, PieceType.ROOK);
        pieceType.put(Piece.BLACK_QUEEN, PieceType.QUEEN);
        pieceType.put(Piece.BLACK_KING, PieceType.KING);

        pieceSide.put(Piece.WHITE_PAWN, Side.WHITE);
        pieceSide.put(Piece.WHITE_KNIGHT, Side.WHITE);
        pieceSide.put(Piece.WHITE_BISHOP, Side.WHITE);
        pieceSide.put(Piece.WHITE_ROOK, Side.WHITE);
        pieceSide.put(Piece.WHITE_QUEEN, Side.WHITE);
        pieceSide.put(Piece.WHITE_KING, Side.WHITE);

        pieceSide.put(Piece.BLACK_PAWN, Side.BLACK);
        pieceSide.put(Piece.BLACK_KNIGHT, Side.BLACK);
        pieceSide.put(Piece.BLACK_BISHOP, Side.BLACK);
        pieceSide.put(Piece.BLACK_ROOK, Side.BLACK);
        pieceSide.put(Piece.BLACK_QUEEN, Side.BLACK);
        pieceSide.put(Piece.BLACK_KING, Side.BLACK);


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
        return pieceType.get(this);
    }

    /**
     * Gets piece side.
     *
     * @return the piece side
     */
    public Side getPieceSide() {
        return pieceSide.get(this);
    }
}
