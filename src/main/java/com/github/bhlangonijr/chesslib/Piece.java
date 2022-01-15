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
 * A chess piece on the board, that is, a specific combination of a {@link Side} and a {@link PieceType}.
 * <p>
 * Each value defines a single piece, except for the special value {@link Piece#NONE} which identifies that no piece is
 * selected or assigned.
 */
public enum Piece {

    /**
     * A white pawn.
     */
    WHITE_PAWN(Side.WHITE, PieceType.PAWN, "♙", "P"),
    /**
     * A white knight.
     */
    WHITE_KNIGHT(Side.WHITE, PieceType.KNIGHT, "♘", "N"),
    /**
     * A white bishop.
     */
    WHITE_BISHOP(Side.WHITE, PieceType.BISHOP, "♗", "B"),
    /**
     * A white rook.
     */
    WHITE_ROOK(Side.WHITE, PieceType.ROOK, "♖", "R"),
    /**
     * A white queen.
     */
    WHITE_QUEEN(Side.WHITE, PieceType.QUEEN, "♕", "Q"),
    /**
     * A white king.
     */
    WHITE_KING(Side.WHITE, PieceType.KING, "♔", "K"),
    /**
     * A black pawn.
     */
    BLACK_PAWN(Side.BLACK, PieceType.PAWN, "♟", "p"),
    /**
     * A black knight.
     */
    BLACK_KNIGHT(Side.BLACK, PieceType.KNIGHT, "♞", "n"),
    /**
     * A black bishop.
     */
    BLACK_BISHOP(Side.BLACK, PieceType.BISHOP, "♝", "b"),
    /**
     * A black rook.
     */
    BLACK_ROOK(Side.BLACK, PieceType.ROOK, "♜", "r"),
    /**
     * A black queen.
     */
    BLACK_QUEEN(Side.BLACK, PieceType.QUEEN, "♛", "q"),
    /**
     * A black king.
     */
    BLACK_KING(Side.BLACK, PieceType.KING, "♚", "k"),
    /**
     * Special value that represents no piece in particular.
     */
    NONE(null, null, "NONE", ".");

    public static final Piece[] allPieces = values();
    private static final Map<String, Piece> fenToPiece = new HashMap<>(13);
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
     * Returns a piece given its name.
     * <p>
     * Same as invoking {@link Piece#valueOf(String)}.
     *
     * @param v name of the piece
     * @return the piece with the specified name
     * @throws IllegalArgumentException if the name does not correspond to any piece
     */
    public static Piece fromValue(String v) {
        return valueOf(v);
    }

    /**
     * Returns the piece corresponding to the provided pair of side and type. If {@link PieceType#NONE} is requested,
     * {@link Piece#NONE} is returned regardless of the side.
     *
     * @param side the side of the wanted piece
     * @param type the type of the wanted piece
     * @return the piece corresponding to the given combination of side and type
     */
    public static Piece make(Side side, PieceType type) {
        return pieceMake[type.ordinal()][side.ordinal()];
    }

    /**
     * Returns the piece corresponding to the given Forsyth-Edwards Notation (FEN) symbol.
     *
     * @param fenSymbol a piece FEN symbol, such as {@code K}, {@code b} or {@code p}
     * @return the piece that corresponds to the FEN symbol provided in input
     * @throws IllegalArgumentException if the input symbol does not correspond to any standard chess piece
     */
    public static Piece fromFenSymbol(String fenSymbol) {
        final Piece piece = fenToPiece.get(fenSymbol);
        if (piece == null) {
            throw new IllegalArgumentException(String.format("Unknown piece '%s'", fenSymbol));
        }
        return piece;
    }

    /**
     * Returns the name of the piece.
     *
     * @return the name of the piece
     */
    public String value() {
        return name();
    }

    /**
     * Returns the type of this piece.
     *
     * @return the piece type of this piece
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Returns the side of this piece.
     *
     * @return the side of this piece
     */
    public Side getPieceSide() {
        return side;
    }

    /**
     * Returns the Short Algebraic Notation (SAN) symbol for this piece. For example, {@code R} for a rook, {@code K}
     * for a king, or an empty string for a pawn.
     *
     * @return the SAN symbol of this piece
     */
    public String getSanSymbol() {
        return type.getSanSymbol();
    }

    /**
     * Returns the Figurine Algebraic Notation (FAN) symbol for this piece. For example, {@code ♜} for a black rook, or
     * {@code ♙} for a white pawn.
     *
     * @return the FAN symbol of this piece
     */
    public String getFanSymbol() {
        return fanSymbol;
    }

    /**
     * Returns the Forsyth-Edwards Notation (FEN) symbol for this piece. For example, {@code r} for a black rook, or
     * {@code P} for a white pawn.
     *
     * @return the FEN symbol of this piece
     */
    public String getFenSymbol() {
        return fenSymbol;
    }

}
