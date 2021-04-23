package com.github.bhlangonijr.chesslib;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PieceTypeTest {

    @Test
    public void fromSanSymbol() {
        assertEquals(PieceType.PAWN, PieceType.fromSanSymbol(""));
        assertEquals(PieceType.KNIGHT, PieceType.fromSanSymbol("N"));
        assertEquals(PieceType.BISHOP, PieceType.fromSanSymbol("B"));
        assertEquals(PieceType.ROOK, PieceType.fromSanSymbol("R"));
        assertEquals(PieceType.QUEEN, PieceType.fromSanSymbol("Q"));
        assertEquals(PieceType.KING, PieceType.fromSanSymbol("K"));
        assertEquals(PieceType.NONE, PieceType.fromSanSymbol("NONE"));

        try {
            PieceType.fromSanSymbol("X");
            fail("There should have been an exception");
        } catch (Exception expected) {
            assertTrue(expected instanceof IllegalArgumentException);
            assertEquals("Unknown piece 'X'", expected.getMessage());
        }
    }
}