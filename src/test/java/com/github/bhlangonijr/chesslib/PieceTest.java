package com.github.bhlangonijr.chesslib;

import org.junit.Test;

import static org.junit.Assert.*;

public class PieceTest {

    @Test
    public void getPieceType() {
        assertEquals(PieceType.PAWN, Piece.WHITE_PAWN.getPieceType());
        assertEquals(PieceType.PAWN, Piece.BLACK_PAWN.getPieceType());
        assertEquals(PieceType.KNIGHT, Piece.WHITE_KNIGHT.getPieceType());
        assertEquals(PieceType.KNIGHT, Piece.BLACK_KNIGHT.getPieceType());
        assertEquals(PieceType.BISHOP, Piece.WHITE_BISHOP.getPieceType());
        assertEquals(PieceType.BISHOP, Piece.BLACK_BISHOP.getPieceType());
        assertEquals(PieceType.ROOK, Piece.WHITE_ROOK.getPieceType());
        assertEquals(PieceType.ROOK, Piece.BLACK_ROOK.getPieceType());
        assertEquals(PieceType.QUEEN, Piece.WHITE_QUEEN.getPieceType());
        assertEquals(PieceType.QUEEN, Piece.BLACK_QUEEN.getPieceType());
        assertEquals(PieceType.KING, Piece.WHITE_KING.getPieceType());
        assertEquals(PieceType.KING, Piece.BLACK_KING.getPieceType());
        assertNull(Piece.NONE.getPieceType());
    }

    @Test
    public void getPieceSide() {
        assertEquals(Side.WHITE, Piece.WHITE_PAWN.getPieceSide());
        assertEquals(Side.BLACK, Piece.BLACK_PAWN.getPieceSide());
        assertEquals(Side.WHITE, Piece.WHITE_KNIGHT.getPieceSide());
        assertEquals(Side.BLACK, Piece.BLACK_KNIGHT.getPieceSide());
        assertEquals(Side.WHITE, Piece.WHITE_BISHOP.getPieceSide());
        assertEquals(Side.BLACK, Piece.BLACK_BISHOP.getPieceSide());
        assertEquals(Side.WHITE, Piece.WHITE_ROOK.getPieceSide());
        assertEquals(Side.BLACK, Piece.BLACK_ROOK.getPieceSide());
        assertEquals(Side.WHITE, Piece.WHITE_QUEEN.getPieceSide());
        assertEquals(Side.BLACK, Piece.BLACK_QUEEN.getPieceSide());
        assertEquals(Side.WHITE, Piece.WHITE_KING.getPieceSide());
        assertEquals(Side.BLACK, Piece.BLACK_KING.getPieceSide());
        assertNull(Piece.NONE.getPieceSide());
    }

    @Test
    public void make() {
        assertEquals(Piece.WHITE_PAWN, Piece.make(Side.WHITE, PieceType.PAWN));
        assertEquals(Piece.BLACK_PAWN, Piece.make(Side.BLACK, PieceType.PAWN));
        assertEquals(Piece.WHITE_KNIGHT, Piece.make(Side.WHITE, PieceType.KNIGHT));
        assertEquals(Piece.BLACK_KNIGHT, Piece.make(Side.BLACK, PieceType.KNIGHT));
        assertEquals(Piece.WHITE_BISHOP, Piece.make(Side.WHITE, PieceType.BISHOP));
        assertEquals(Piece.BLACK_BISHOP, Piece.make(Side.BLACK, PieceType.BISHOP));
        assertEquals(Piece.WHITE_ROOK, Piece.make(Side.WHITE, PieceType.ROOK));
        assertEquals(Piece.BLACK_ROOK, Piece.make(Side.BLACK, PieceType.ROOK));
        assertEquals(Piece.WHITE_QUEEN, Piece.make(Side.WHITE, PieceType.QUEEN));
        assertEquals(Piece.BLACK_QUEEN, Piece.make(Side.BLACK, PieceType.QUEEN));
        assertEquals(Piece.WHITE_KING, Piece.make(Side.WHITE, PieceType.KING));
        assertEquals(Piece.BLACK_KING, Piece.make(Side.BLACK, PieceType.KING));
        assertEquals(Piece.NONE, Piece.make(Side.WHITE, PieceType.NONE));
        assertEquals(Piece.NONE, Piece.make(Side.BLACK, PieceType.NONE));
    }

    @Test
    public void fromValue() {
        assertEquals(Piece.WHITE_PAWN, Piece.fromValue("WHITE_PAWN"));
        assertEquals(Piece.BLACK_PAWN, Piece.fromValue("BLACK_PAWN"));
        assertEquals(Piece.WHITE_KNIGHT, Piece.fromValue("WHITE_KNIGHT"));
        assertEquals(Piece.BLACK_KNIGHT, Piece.fromValue("BLACK_KNIGHT"));
        assertEquals(Piece.WHITE_BISHOP, Piece.fromValue("WHITE_BISHOP"));
        assertEquals(Piece.BLACK_BISHOP, Piece.fromValue("BLACK_BISHOP"));
        assertEquals(Piece.WHITE_ROOK, Piece.fromValue("WHITE_ROOK"));
        assertEquals(Piece.BLACK_ROOK, Piece.fromValue("BLACK_ROOK"));
        assertEquals(Piece.WHITE_QUEEN, Piece.fromValue("WHITE_QUEEN"));
        assertEquals(Piece.BLACK_QUEEN, Piece.fromValue("BLACK_QUEEN"));
        assertEquals(Piece.WHITE_KING, Piece.fromValue("WHITE_KING"));
        assertEquals(Piece.BLACK_KING, Piece.fromValue("BLACK_KING"));
        assertEquals(Piece.NONE, Piece.fromValue("NONE"));
    }

    @Test
    public void value() {
        assertEquals("WHITE_PAWN", Piece.WHITE_PAWN.value());
        assertEquals("BLACK_PAWN", Piece.BLACK_PAWN.value());
        assertEquals("WHITE_KNIGHT", Piece.WHITE_KNIGHT.value());
        assertEquals("BLACK_KNIGHT", Piece.BLACK_KNIGHT.value());
        assertEquals("WHITE_BISHOP", Piece.WHITE_BISHOP.value());
        assertEquals("BLACK_BISHOP", Piece.BLACK_BISHOP.value());
        assertEquals("WHITE_ROOK", Piece.WHITE_ROOK.value());
        assertEquals("BLACK_ROOK", Piece.BLACK_ROOK.value());
        assertEquals("WHITE_QUEEN", Piece.WHITE_QUEEN.value());
        assertEquals("BLACK_QUEEN", Piece.BLACK_QUEEN.value());
        assertEquals("WHITE_KING", Piece.WHITE_KING.value());
        assertEquals("BLACK_KING", Piece.BLACK_KING.value());
        assertEquals("NONE", Piece.NONE.value());
    }

    @Test
    public void fromFenSymbol() {
        assertEquals(Piece.WHITE_PAWN, Piece.fromFenSymbol("P"));
        assertEquals(Piece.WHITE_KNIGHT, Piece.fromFenSymbol("N"));
        assertEquals(Piece.WHITE_BISHOP, Piece.fromFenSymbol("B"));
        assertEquals(Piece.WHITE_ROOK, Piece.fromFenSymbol("R"));
        assertEquals(Piece.WHITE_QUEEN, Piece.fromFenSymbol("Q"));
        assertEquals(Piece.WHITE_KING, Piece.fromFenSymbol("K"));
        assertEquals(Piece.BLACK_PAWN, Piece.fromFenSymbol("p"));
        assertEquals(Piece.BLACK_KNIGHT, Piece.fromFenSymbol("n"));
        assertEquals(Piece.BLACK_BISHOP, Piece.fromFenSymbol("b"));
        assertEquals(Piece.BLACK_ROOK, Piece.fromFenSymbol("r"));
        assertEquals(Piece.BLACK_QUEEN, Piece.fromFenSymbol("q"));
        assertEquals(Piece.BLACK_KING, Piece.fromFenSymbol("k"));
        assertEquals(Piece.NONE, Piece.fromFenSymbol("."));

        try {
            Piece.fromFenSymbol("X");
            fail("There should have been an exception");
        } catch (Exception expected) {
            assertTrue(expected instanceof IllegalArgumentException);
            assertEquals("Unknown piece 'X'", expected.getMessage());
        }
    }
}