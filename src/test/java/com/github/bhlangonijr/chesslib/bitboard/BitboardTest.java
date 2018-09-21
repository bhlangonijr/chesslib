package com.github.bhlangonijr.chesslib.bitboard;

import com.github.bhlangonijr.chesslib.Bitboard;
import com.github.bhlangonijr.chesslib.Square;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * The type Bitboard test.
 */
public class BitboardTest {

    /**
     * Test bb functions.
     */
    @Test
    public void testBBFunctions() {

        for (int x = 0; x < 64; x++) {
            assertEquals(Bitboard.bitScanForward(1L << x), x);
            assertEquals(Bitboard.bitScanReverse(1L << x), x);
        }
        long t = (1L << 10) | (1L << 20);
        long lsb = Bitboard.extractLsb(t);

        assertEquals(1L << 20, lsb);

        lsb = Bitboard.extractLsb(0L);

        assertEquals(0L, lsb);

        long ba = Bitboard.getBishopAttacks(0L, Square.D5);

        assertEquals(Bitboard.bitboardToString(ba),
                "00000001\n" +
                        "10000010\n" +
                        "01000100\n" +
                        "00101000\n" +
                        "00000000\n" +
                        "00101000\n" +
                        "01000100\n" +
                        "10000010\n");

        long ra = Bitboard.getRookAttacks(0L, Square.D5);

        assertEquals(Bitboard.bitboardToString(ra),
                "00010000\n" +
                        "00010000\n" +
                        "00010000\n" +
                        "00010000\n" +
                        "11101111\n" +
                        "00010000\n" +
                        "00010000\n" +
                        "00010000\n");

    }

}