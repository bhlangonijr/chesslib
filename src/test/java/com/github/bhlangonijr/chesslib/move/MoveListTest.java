package com.github.bhlangonijr.chesslib.move;

import com.github.bhlangonijr.chesslib.Square;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * The type Move list test.
 */
public class MoveListTest {

    /**
     * Test move list starting position.
     *
     * @throws MoveConversionException the move conversion exception
     */
    @Test
    public void testMoveListStartingPosition() throws MoveConversionException {
        String[] san = {"e4", "e4 e6", "e4 e6 f4"};
        String[] fen = {"rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1",
                "rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2",
                "rnbqkbnr/pppp1ppp/4p3/8/4PP2/8/PPPP2PP/RNBQKBNR b KQkq f3 0 2"};

        for (int i = 0; i < 3; i++) {
            MoveList list = new MoveList();
            list.loadFromSan(san[i]);
            list.getFen();
            assertEquals(fen[i], list.getFen());
        }
    }

    /**
     * Test move list small.
     *
     * @throws MoveConversionException the move conversion exception
     */
    @Test
    public void testMoveListSmall() throws MoveConversionException {
        String san = "e4 Nc6 d4 Nf6 d5 Ne5 Nf3 d6 Nxe5 dxe5 Bb5+ Bd7 Bxd7+ Qxd7 Nc3 e6 O-O exd5 ";
        MoveList list1 = new MoveList();
        list1.loadFromSan(san);
        assertEquals("r3kb1r/pppq1ppp/5n2/3pp3/4P3/2N5/PPP2PPP/R1BQ1RK1 w kq - 0 10", list1.getFen());
    }

    /**
     * Test move list.
     *
     * @throws MoveConversionException the move conversion exception
     */
    @Test
    public void testMoveList() throws MoveConversionException {

        String s = "e2e4 b8c6 d2d4 g8f6 d4d5 c6e5 g1f3 d7d6 f3e5 d6e5 f1b5 c8d7 b5d7 d8d7 b1c3 e7e6 e1g1 e6d5";
        String san = "e4 Nc6 d4 Nf6 d5 Ne5 Nf3 d6 Nxe5 dxe5 Bb5+ Bd7 Bxd7+ Qxd7 Nc3 e6 O-O exd5 ";
        MoveList list = new MoveList();
        list.loadFromText(s);
        String mvs[] = s.split(StringUtils.SPACE);
        int i = 0;
        for (Move move : list) {
            assertEquals(mvs[i++], move.toString());
        }
        assertEquals(san, list.toSan());
        MoveList list1 = new MoveList();
        list1.loadFromSan(san);
        i = 0;
        for (Move move : list1) {
            assertEquals(mvs[i++], move.toString());
        }

    }

    /**
     * Test that a SAN move list can be returned with move numbers.
     */
    @Test
    public void testToSanWithMoveNumbers() {
        // No moves yet:
        String expectedSan = StringUtils.EMPTY;
        MoveList list = new MoveList();
        assertEquals(expectedSan, list.toSan());
        assertEquals(expectedSan, list.toSanWithMoveNumbers());

        // Ends with a move by Black:
        String s = "e2e4 b8c6 d2d4 g8f6 d4d5 c6e5 g1f3 d7d6 f3e5 d6e5 f1b5 c8d7 b5d7 d8d7 b1c3 e7e6";
        expectedSan = "1. e4 Nc6 2. d4 Nf6 3. d5 Ne5 4. Nf3 d6 5. Nxe5 dxe5 6. Bb5+ Bd7 7. Bxd7+ Qxd7 8. Nc3 e6 ";
        list = new MoveList();
        list.loadFromText(s);
        assertEquals(expectedSan, list.toSanWithMoveNumbers());

        // Ends with a move by White:
        s = "e2e4 b8c6 d2d4 g8f6 d4d5 c6e5 g1f3 d7d6 f3e5 d6e5 f1b5 c8d7 b5d7 d8d7 b1c3";
        expectedSan = "1. e4 Nc6 2. d4 Nf6 3. d5 Ne5 4. Nf3 d6 5. Nxe5 dxe5 6. Bb5+ Bd7 7. Bxd7+ Qxd7 8. Nc3 ";
        list = new MoveList();
        list.loadFromText(s);
        assertEquals(expectedSan, list.toSanWithMoveNumbers());

        // Read a SAN string with numbers, assert that the same string is returned:
        list = new MoveList();
        list.loadFromSan(expectedSan);
        assertEquals(expectedSan, list.toSanWithMoveNumbers());
    }

    /**
     * Test that a FAN move list can be returned with move numbers.
     */
    @Test
    public void testToFanWithMoveNumbers() {
        // No moves yet:
        String expectedFan = StringUtils.EMPTY;
        MoveList list = new MoveList();
        assertEquals(expectedFan, list.toFan());
        assertEquals(expectedFan, list.toFanWithMoveNumbers());

        // Ends with a move by Black:
        String s = "e2e4 b8c6 d2d4 g8f6 d4d5 c6e5 g1f3 d7d6 f3e5 d6e5 f1b5 c8d7 b5d7 d8d7 b1c3 e7e6";
        expectedFan = "1. ♙e4 ♞c6 2. ♙d4 ♞f6 3. ♙d5 ♞e5 4. ♘f3 ♟d6 5. ♘xe5 ♟dxe5 6. ♗b5+ ♝d7 7. ♗xd7+ ♛xd7 8. ♘c3 ♟e6 ";
        list = new MoveList();
        list.loadFromText(s);
        assertEquals(expectedFan, list.toFanWithMoveNumbers());

        // Ends with a move by White:
        s = "e2e4 b8c6 d2d4 g8f6 d4d5 c6e5 g1f3 d7d6 f3e5 d6e5 f1b5 c8d7 b5d7 d8d7 b1c3";
        expectedFan = "1. ♙e4 ♞c6 2. ♙d4 ♞f6 3. ♙d5 ♞e5 4. ♘f3 ♟d6 5. ♘xe5 ♟dxe5 6. ♗b5+ ♝d7 7. ♗xd7+ ♛xd7 8. ♘c3 ";
        list = new MoveList();
        list.loadFromText(s);
        assertEquals(expectedFan, list.toFanWithMoveNumbers());
    }

    /**
     * Test move list pgn 1.
     *
     * @throws MoveConversionException the move conversion exception
     */
    @Test
    public void testMoveListPGN1() throws MoveConversionException {
        String moveText = "1.d4 d5 2.c4 c6 3.Nf3 Nf6 4.e3 Bf5 5.Nc3 e6 6.Nh4 Bg6 7.Nxg6 " +
                "hxg6 8.g3 Nbd7 9.Bg2 dxc4 10.Qe2 Nb6 11.O-O Be7 12.Rd1 Qc7 13.a4 " +
                "a5 14.e4 e5 15.Be3 Nfd7 16.h4 O-O 17.dxe5 Nxe5 18.f4 Nd3 19.e5 " +
                "Bc5 20.Be4 Bxe3+ 21.Qxe3 Nxb2 22.Rdb1 N2xa4 23.Nxa4 Nxa4 24.Rxa4 " +
                "b5 25.Ra2 b4 26.Qe2 c3 27.h5 Qb6+ 28.Kg2 b3 29.Ra3 c2 30.Rh1 " +
                "g5 31.Qc4 Rfb8 32.Bg6 Ra7 33.e6 Qb5 34.exf7+ Kf8 35.Rxb3 Qxb3 " +
                "36.Re1 Rxf7 37.Qc5+ Kg8 38.Bxf7+ Qxf7 39.Qxc2 gxf4 40.Qxc6 fxg3 " +
                "41.Qe6 a4 42.Qe5 Qf2+ 43.Kh3 Qh2+ 44.Kg4 Rb4+ 45.Re4 Qe2+ 46.Kg5 " +
                "Qxe4 47.Qxg7+ Kxg7 48.h6+ Kh8";
        MoveList list = new MoveList();
        list.loadFromSan(moveText);
        assertEquals("7k/8/7P/6K1/pr2q3/6p1/8/8 w - - 1 49", list.getFen());
    }

    /**
     * Test move list pgn 2.
     *
     * @throws MoveConversionException the move conversion exception
     */
    @Test
    public void testMoveListPGN2() throws MoveConversionException {
        String moveText = "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 a6 6.Bc4 Nbd7 7.O-O " +
                "g6 8.a3 Bg7 9.Ba2 O-O 10.Be3 Ne5 11.Bg5 h6 12.Bxf6 Bxf6 13.Nd5 " +
                "Bg7 14.c3 Nc6 15.Nc2 e6 16.Nde3 b5 17.Qd2 Bb7 18.Rad1 Qb6 19.Kh1 " +
                "Ne7 20.Qxd6 Qxd6 21.Rxd6 Bxe4 22.f3 Nc8 23.Rd2 Bb7 24.Rfd1 Nb6 " +
                "25.Bb3 h5 26.Rd6 Nc8 27.Rd8 Bf6 28.Rxf8+ Kxf8 29.Kg1 Ke7 30.a4 " +
                "bxa4 31.Bxa4 Nb6 32.Bb3 a5 33.Nc4 Nxc4 34.Bxc4 Bc6 35.Nd4 Be8 " +
                "36.b3 Rc8 37.Ne2 a4 38.Rc1 Kd6 39.Kf2 Rxc4 40.bxc4 Kc5 41.Rb1 " +
                "Kxc4 42.Rb4+ Kc5 43.Rb7 Be5 44.f4 Bd6 45.Ke3 a3 46.Rb4 a2 47.Nc1 " +
                "a1N 48.Nd3+ Kd5 49.Rd4+ Kc6 50.Ra4 Nb3 51.Ra8 Kd7 52.Ne5+ Ke7 " +
                "53.Ra7+ Kf8 54.Ra8 Nc5 55.g3 g5 56.Nc4 gxf4+ 57.gxf4 Bc7 58.Rc8 " +
                "Na6 59.Ra8 Nb8 60.Ra7 Bd8 61.Ra8 Nc6 62.Nd6 Ke7 63.Nxe8 Kxe8 " +
                "64.f5 Ke7 65.Ke4 Kd6 66.Ra1 Bf6 67.Rd1+ Ke7 68.Rd3 Ne5 69.Rh3 " +
                "h4 70.fxe6 Kxe6 71.Re3 Bg5 72.Re2 h3 73.Kd4 Bf4 74.Ke4 Bg5 75.Kd4 " +
                "f6 76.c4 Kf5 77.c5 Bf4 78.Kd5 Kg4 79.Ke6 Nf3 80.Kxf6 Nxh2 81.Re4 " +
                "Kf3 82.Kf5 Bg3 83.Re7 Ng4 84.Rh7 h2 85.c6 Ne3+ 86.Ke6 Kg2 87.Kd7 " +
                "Nd5 88.Ke6 Nc7+";
        MoveList list = new MoveList();
        list.loadFromSan(moveText);
        assertEquals("8/2n4R/2P1K3/8/8/6b1/6kp/8 w - - 7 89", list.getFen());
    }

    /**
     * Test move list pgn 3.
     *
     * @throws MoveConversionException the move conversion exception
     */
    @Test
    public void testMoveListPGN3() throws MoveConversionException {
        String moveText = "1.Nf3 d6 2.d4 Bg4 3.c4 Nd7 4.e4 Bxf3 5.Qxf3 g6 6.Nc3 Bg7 7.Qd1 " +
                "c5 8.d5 Bxc3+ 9.bxc3 Qa5 10.Qb3 O-O-O 11.Be2 Ngf6 12.f3 Rdg8 " +
                "13.h4 h6 14.Bd2 Nh5 15.Rb1 Qc7 16.Kf2 f5 17.exf5 gxf5 18.Bd3 " +
                "Rf8 19.Qc2 f4 20.Rb2 Ng7 21.Rhb1 b6 22.Re1 Ne5 23.a4 e6 24.a5 " +
                "exd5 25.axb6 axb6 26.cxd5 c4 27.Bg6 Qc5+ 28.Kf1 Rf6 29.Qa4 Rxg6 " +
                "30.Qa6+ Kd7 31.Rxb6 Qxd5 32.Rb7+ Ke6 33.Rxe5+ Qxe5 34.Qxc4+ d5 " +
                "35.Qa6+ Kf5 36.Rf7+ Rf6 37.Qd3+ Ke6 38.Rxg7 Ra8 39.Qh7 Ra1+ 40.Kf2 " +
                "Qb8 41.Rb7 Qd8 42.c4 Ra2 43.cxd5+ Kd6 44.Qd3 Qa5 45.Ke1 Ra1+ " +
                "46.Ke2 Ra2 47.Ke1 Ra1+ 48.Ke2 Ra2 49.Rb1 Rf5 50.Kf1 Qxd5 51.Bxf4+ " +
                "Kc6 52.Rc1+ Kd7 53.Qxd5+ Rxd5 54.Bxh6 Re5 55.Rd1+ Ke6 56.Rd2 " +
                "Ra4 57.Kf2 Rxh4 58.Be3 ";
        MoveList list = new MoveList();
        list.loadFromSan(moveText);
        assertEquals("8/8/4k3/4r3/7r/4BP2/3R1KP1/8 b - - 1 58", list.getFen());

    }

    @Test
    public void testEncodingSanAmbiguityResolution() {

        final MoveList moveList = new MoveList("4k3/8/8/8/1b6/2N5/8/4K1N1 w - - 0 1");
        moveList.add(new Move(Square.G1, Square.E2)); // Ne2
        assertEquals("Ne2", moveList.toSan().trim());
    }

    @Test
    public void testDecodingSanAmbiguityResolution() {

        final MoveList moveList = new MoveList("4k3/8/8/8/1b6/2N5/8/4K1N1 w - - 0 1");
        moveList.addSanMove("Nge2", true, true);
        assertEquals("Ne2", moveList.toSanArray()[moveList.size() - 1]);
    }

    @Test(expected = MoveConversionException.class)
    public void testInvalidSan() {

        final MoveList moveList = new MoveList("4k3/8/8/8/1b6/2N5/8/4K1N1 w - - 0 1");
        moveList.addSanMove("Nce2", false, true);
    }

    @Test
    public void testSanCastleWithMate() {

        final String san = "1. d4 e6 2. Nf3 f5 3. Nc3 Nf6 4. Bg5 Be7 5. Bxf6 Bxf6 6. e4 fxe4 7. Nxe4 b6 "
                + "8. Ne5 O-O 9. Bd3 Bb7 10. Qh5 Qe7 11. Qxh7+ Kxh7 12. Nxf6+ Kh6 13. Neg4+ Kg5 14. h4+ Kf4 "
                + "15. g3+ Kf3 16. Be2+ Kg2 17. Rh2+ Kg1 18. O-O-O#";

        final MoveList moveList = new MoveList();
        moveList.loadFromSan(san);
        final String sanGeneratedLastMove = moveList.toSanArray()[moveList.toSanArray().length - 1];
        assertEquals("O-O-O#", sanGeneratedLastMove);
    }

}
