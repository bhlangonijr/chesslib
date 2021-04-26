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

import com.github.bhlangonijr.chesslib.move.Move;

import java.util.*;

/**
 * The type Constants.
 */
public class Constants {

    /**
     * The constant startStandardFENPosition.
     */
    public static final String startStandardFENPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    /**
     * The constant DEFAULT_WHITE_OO.
     */
    public static final Move DEFAULT_WHITE_OO = new Move(Square.E1, Square.G1);
    /**
     * The constant DEFAULT_WHITE_OOO.
     */
    public static final Move DEFAULT_WHITE_OOO = new Move(Square.E1, Square.C1);
    /**
     * The constant DEFAULT_BLACK_OO.
     */
    public static final Move DEFAULT_BLACK_OO = new Move(Square.E8, Square.G8);
    /**
     * The constant DEFAULT_BLACK_OOO.
     */
    public static final Move DEFAULT_BLACK_OOO = new Move(Square.E8, Square.C8);
    /**
     * The constant DEFAULT_WHITE_ROOK_OO.
     */
    public static final Move DEFAULT_WHITE_ROOK_OO = new Move(Square.H1, Square.F1);
    /**
     * The constant DEFAULT_WHITE_ROOK_OOO.
     */
    public static final Move DEFAULT_WHITE_ROOK_OOO = new Move(Square.A1, Square.D1);
    /**
     * The constant DEFAULT_BLACK_ROOK_OO.
     */
    public static final Move DEFAULT_BLACK_ROOK_OO = new Move(Square.H8, Square.F8);
    /**
     * The constant DEFAULT_BLACK_ROOK_OOO.
     */
    public static final Move DEFAULT_BLACK_ROOK_OOO = new Move(Square.A8, Square.D8);
    /**
     * The constant DEFAULT_WHITE_OO_SQUARES.
     */
    public static final List<Square> DEFAULT_WHITE_OO_SQUARES = new ArrayList<Square>();
    /**
     * The constant DEFAULT_WHITE_OOO_SQUARES.
     */
    public static final List<Square> DEFAULT_WHITE_OOO_SQUARES = new ArrayList<Square>();
    /**
     * The constant DEFAULT_BLACK_OO_SQUARES.
     */
    public static final List<Square> DEFAULT_BLACK_OO_SQUARES = new ArrayList<Square>();
    /**
     * The constant DEFAULT_BLACK_OOO_SQUARES.
     */
    public static final List<Square> DEFAULT_BLACK_OOO_SQUARES = new ArrayList<Square>();

    /**
     * The constant DEFAULT_WHITE_OO_ALL_SQUARES.
     */
    public static final List<Square> DEFAULT_WHITE_OO_ALL_SQUARES = new ArrayList<Square>();
    /**
     * The constant DEFAULT_WHITE_OOO_ALL_SQUARES.
     */
    public static final List<Square> DEFAULT_WHITE_OOO_ALL_SQUARES = new ArrayList<Square>();
    /**
     * The constant DEFAULT_BLACK_OO_ALL_SQUARES.
     */
    public static final List<Square> DEFAULT_BLACK_OO_ALL_SQUARES = new ArrayList<Square>();
    /**
     * The constant DEFAULT_BLACK_OOO_ALL_SQUARES.
     */
    public static final List<Square> DEFAULT_BLACK_OOO_ALL_SQUARES = new ArrayList<Square>();

    /**
     * The constant pieceNotation.
     *
     * @deprecated Use {@link Piece#getFenSymbol()} instead.
     */
    @Deprecated
    public static final EnumMap<Piece, String> pieceNotation =
            new EnumMap<Piece, String>(Piece.class);
    /**
     * The constant pieceNotationR.
     *
     * @deprecated Use {@link Piece#fromFenSymbol(String)} instead.
     */
    @Deprecated
    public static final Map<String, Piece> pieceNotationR =
            new HashMap<String, Piece>(12);

    public static final Move emptyMove = new Move(Square.NONE, Square.NONE);

    static {

        DEFAULT_WHITE_OO_SQUARES.add(Square.F1);
        DEFAULT_WHITE_OO_SQUARES.add(Square.G1);
        DEFAULT_WHITE_OOO_SQUARES.add(Square.D1);
        DEFAULT_WHITE_OOO_SQUARES.add(Square.C1);

        DEFAULT_BLACK_OO_SQUARES.add(Square.F8);
        DEFAULT_BLACK_OO_SQUARES.add(Square.G8);
        DEFAULT_BLACK_OOO_SQUARES.add(Square.D8);
        DEFAULT_BLACK_OOO_SQUARES.add(Square.C8);

        DEFAULT_WHITE_OO_ALL_SQUARES.add(Square.F1);
        DEFAULT_WHITE_OO_ALL_SQUARES.add(Square.G1);
        DEFAULT_WHITE_OOO_ALL_SQUARES.add(Square.D1);
        DEFAULT_WHITE_OOO_ALL_SQUARES.add(Square.C1);
        DEFAULT_WHITE_OOO_ALL_SQUARES.add(Square.B1);

        DEFAULT_BLACK_OO_ALL_SQUARES.add(Square.F8);
        DEFAULT_BLACK_OO_ALL_SQUARES.add(Square.G8);
        DEFAULT_BLACK_OOO_ALL_SQUARES.add(Square.D8);
        DEFAULT_BLACK_OOO_ALL_SQUARES.add(Square.C8);
        DEFAULT_BLACK_OOO_ALL_SQUARES.add(Square.B8);

        pieceNotation.put(Piece.WHITE_PAWN, "P");
        pieceNotation.put(Piece.WHITE_KNIGHT, "N");
        pieceNotation.put(Piece.WHITE_BISHOP, "B");
        pieceNotation.put(Piece.WHITE_ROOK, "R");
        pieceNotation.put(Piece.WHITE_QUEEN, "Q");
        pieceNotation.put(Piece.WHITE_KING, "K");
        pieceNotation.put(Piece.BLACK_PAWN, "p");
        pieceNotation.put(Piece.BLACK_KNIGHT, "n");
        pieceNotation.put(Piece.BLACK_BISHOP, "b");
        pieceNotation.put(Piece.BLACK_ROOK, "r");
        pieceNotation.put(Piece.BLACK_QUEEN, "q");
        pieceNotation.put(Piece.BLACK_KING, "k");

        pieceNotationR.put("P", Piece.WHITE_PAWN);
        pieceNotationR.put("N", Piece.WHITE_KNIGHT);
        pieceNotationR.put("B", Piece.WHITE_BISHOP);
        pieceNotationR.put("R", Piece.WHITE_ROOK);
        pieceNotationR.put("Q", Piece.WHITE_QUEEN);
        pieceNotationR.put("K", Piece.WHITE_KING);
        pieceNotationR.put("p", Piece.BLACK_PAWN);
        pieceNotationR.put("n", Piece.BLACK_KNIGHT);
        pieceNotationR.put("b", Piece.BLACK_BISHOP);
        pieceNotationR.put("r", Piece.BLACK_ROOK);
        pieceNotationR.put("q", Piece.BLACK_QUEEN);
        pieceNotationR.put("k", Piece.BLACK_KING);
    }

    private Constants() {
    }

    /**
     * Gets the Forsyth-Edwards notation of a piece
     *
     * @param piece the piece
     * @return piece notation
     * @deprecated Use {@link Piece#getFenSymbol()} instead.
     */
    @Deprecated
    public static String getPieceNotation(Piece piece) {
        return piece.getFenSymbol();
    }

    /**
     * Gets the piece by its Forsyth-Edwards notation
     *
     * @param notation the notation
     * @return piece by notation
     * @deprecated Use {@link Piece#fromFenSymbol(String)} instead.
     */
    @Deprecated
    public static Piece getPieceByNotation(String notation) {
        return Piece.fromFenSymbol(notation);
    }
}
