package com.github.bhlangonijr.chesslib;

import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveConversionException;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import com.github.bhlangonijr.chesslib.move.MoveList;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * The type Board test.
 */
public class BoardTest {

    /**
     * Test move and fen parsing.
     */
    @Test
    public void testMoveAndFENParsing() {

        String fen1 = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
        String fen2 = "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2";
        String fen3 = "rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 2";
        String fen4 = "rnbqkbnr/ppp1pppp/8/3pP3/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 2";
        String fen5 = "rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3";
        String fen6 = "rnbqkbnr/ppp1p1pp/5P2/3p4/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 3";
        String fen7 = "rnbqkbnr/ppp3pp/5p2/3p4/8/8/PPPP1PPP/RNBQKBNR w KQkq - 0 4";

        Board board = new Board();

        board.loadFromFen(fen1);

        assertEquals(Piece.BLACK_BISHOP, board.getPiece(Square.C8));
        assertEquals(Piece.WHITE_BISHOP, board.getPiece(Square.C1));
        assertEquals(Piece.BLACK_ROOK, board.getPiece(Square.H8));
        assertEquals(Piece.WHITE_ROOK, board.getPiece(Square.H1));

        assertEquals(new Integer(0), board.getHalfMoveCounter());
        assertEquals(new Integer(1), board.getMoveCounter());
        assertEquals(Square.E3, board.getEnPassant());
        assertEquals(Square.E4, board.getEnPassantTarget());

        assertEquals(fen1, board.getFen());

        Move move = new Move(Square.E7, Square.E5); //sm: b
        board.doMove(move, true);
        System.out.println("new FEN after: " + move.toString() + ": " + board.getFen());
        System.out.println("hash code is: " + board.hashCode());

        assertEquals(fen2, board.getFen()); //sm: w

        board.undoMove();
        System.out.println("new FEN after: undo: " + board.getFen());
        System.out.println("hash code is: " + board.hashCode());
        assertEquals(fen1, board.getFen()); // sm: b

        move = new Move(Square.D7, Square.D5); //sm: b
        board.doMove(move, true);
        System.out.println("new FEN after: " + move.toString() + ": " + board.getFen());
        System.out.println("hash code is: " + board.hashCode());
        assertEquals(fen3, board.getFen()); // sm: w

        move = new Move(Square.E4, Square.E5); //sm: w
        board.doMove(move, true);
        System.out.println("new FEN after: " + move.toString() + ": " + board.getFen());
        System.out.println("hash code is: " + board.hashCode());
        assertEquals(fen4, board.getFen()); // sm: b

        move = new Move(Square.F7, Square.F5); //sm: b
        board.doMove(move, true);
        System.out.println("new FEN after: " + move.toString() + ": " + board.getFen());
        System.out.println("hash code is: " + board.hashCode());
        assertEquals(fen5, board.getFen()); // sm: w

        move = new Move(Square.E5, Square.F6); //sm: w
        board.doMove(move, true);
        System.out.println("new FEN after: " + move.toString() + ": " + board.getFen());
        System.out.println("hash code is: " + board.hashCode());
        assertEquals(fen6, board.getFen()); // sm: b

        move = new Move(Square.E7, Square.F6); //sm: b
        board.doMove(move, true);
        System.out.println("new FEN after: " + move.toString() + ": " + board.getFen());
        System.out.println("hash code is: " + board.hashCode());
        assertEquals(fen7, board.getFen()); // sm: w

    }

    /**
     * Test castle and fen parsing.
     */
    @Test
    public void testCastleAndFENParsing() {

        String fen1 = "rnbqk2r/ppp1b1pp/5p1n/3p4/8/3B1N2/PPPP1PPP/RNBQK2R w KQkq - 4 1";
        String fen2 = "rnbqk2r/ppp1b1pp/5p1n/3p4/8/3B1N2/PPPP1PPP/RNBQ1RK1 b kq - 5 1";
        String fen3 = "rnbq1rk1/ppp1b1pp/5p1n/3p4/8/3B1N2/PPPP1PPP/RNBQ1RK1 w - - 6 2";

        Board board = new Board();

        board.loadFromFen(fen1);

        assertEquals(fen1, board.getFen());

        Move move = new Move(Square.E1, Square.G1); //sm: b
        board.doMove(move, true);
        System.out.println("new FEN after: " + move.toString() + ": " + board.getFen());
        System.out.println("hash code is: " + board.hashCode());

        assertEquals(fen2, board.getFen()); //sm: w

        move = new Move(Square.E8, Square.G8); //sm: w
        board.doMove(move, true);
        System.out.println("new FEN after: " + move.toString() + ": " + board.getFen());
        System.out.println("hash code is: " + board.hashCode());

        assertEquals(fen3, board.getFen()); //sm: b

        board.undoMove();
        System.out.println("new FEN after: undo: " + board.getFen());
        System.out.println("hash code is: " + board.hashCode());
        assertEquals(fen2, board.getFen()); // sm: w

    }

    /**
     * Test clone.
     */
    @Test
    public void testClone() {
        String fen1 = "rnbqk2r/ppp1b1pp/5p1n/3p4/8/3B1N2/PPPP1PPP/RNBQK2R w KQkq - 4 3";
        Board b1 = new Board();
        b1.loadFromFen(fen1);

        Board b2 = b1.clone();

        assertEquals(b1.hashCode(), b2.hashCode());

    }

    /**
     * Test equality.
     */
    @Test
    public void testEquality() {
        String fen1 = "rnbqk2r/ppp1b1pp/5p1n/3p4/8/3B1N2/PPPP1PPP/RNBQK2R w KQkq - 4 3";
        Board b1 = new Board();
        b1.loadFromFen(fen1);
        System.out.println("hash code is: " + b1.hashCode());

        Board b2 = new Board();
        b2.loadFromFen(fen1);
        System.out.println("hash code is: " + b2.hashCode());

        assertEquals(b1, b2);
        assertEquals(b1.getPositionId(), b2.getPositionId());

    }

    /**
     * Test undo move.
     */
    @Test
    public void testUndoMove() {
        String fen1 = "rnbqkbnr/1p1ppppp/p7/1Pp5/8/8/P1PPPPPP/RNBQKBNR w KQkq c6 0 5";
        Board b1 = new Board();
        b1.loadFromFen(fen1);

        b1.doMove(new Move(Square.B5, Square.A6));
        b1.undoMove();

        assertEquals(fen1, b1.getFen());

    }

    /**
     * Test legal move.
     *
     * @throws MoveGeneratorException the move generator exception
     */
    @Test
    public void testLegalMove() throws MoveGeneratorException {
        String fen1 = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/5Q2/PPPBBPpP/RN2K2R w KQkq - 0 2";
        Board b1 = new Board();
        b1.loadFromFen(fen1);

        List<Move> moves = b1.legalMoves();

        assertEquals(47, moves.size());
    }

    /**
     * Test legal move 1.
     *
     * @throws MoveGeneratorException the move generator exception
     */
    @Test
    public void testLegalMove1() throws MoveGeneratorException {
        String fen = "1r6/3k2p1/7p/Ppp2r1P/K1N1B1p1/2P2NP1/b7/4b3 w - - 0 56";
        Board b = new Board();
        b.loadFromFen(fen);

        List<Move> moves = b.legalMoves();

        assertEquals(new Move(Square.A4, Square.A3), moves.get(0));

    }

    /**
     * Test legal move 3.
     *
     * @throws MoveGeneratorException the move generator exception
     */
    @Test
    public void testLegalMove3() throws MoveGeneratorException {
        String fen = "2r3r3/4n3/p1kp3p/1p3pP1/1p1bPPKP/1PPP4/BR1R4/8 w - - 0 73";
        Board b = new Board();
        b.loadFromFen(fen);

        List<Move> moves = b.legalMoves();

        assertTrue(moves.contains(new Move(Square.E4, Square.F5)));
        assertTrue(moves.contains(new Move(Square.G4, Square.F3)));
        assertTrue(moves.contains(new Move(Square.G4, Square.G3)));
        assertTrue(moves.contains(new Move(Square.G4, Square.H3)));
        assertTrue(moves.contains(new Move(Square.G4, Square.H5)));
    }

    /**
     * Test legal move 4.
     *
     * @throws MoveGeneratorException the move generator exception
     */
    @Test
    public void testLegalMove4() throws MoveGeneratorException {
        String fen = "7k/8/R5Q1/1BpP4/3K4/8/8/8 w - c6 0 0";
        Board b = new Board();
        b.loadFromFen(fen);

        List<Move> moves = b.legalMoves();

        assertTrue(moves.contains(new Move(Square.D5, Square.C6)));
        assertTrue(moves.contains(new Move(Square.D4, Square.C3)));
        assertTrue(moves.contains(new Move(Square.D4, Square.D3)));
        assertTrue(moves.contains(new Move(Square.D4, Square.E3)));
        assertTrue(moves.contains(new Move(Square.D4, Square.C4)));
        assertTrue(moves.contains(new Move(Square.D4, Square.E4)));
        assertTrue(moves.contains(new Move(Square.D4, Square.C5)));
        assertTrue(moves.contains(new Move(Square.D4, Square.E5)));
        assertEquals(8, moves.size());
    }

    @Test
    public void testIncrementalHashKey() {

        String fen = "r1b1kb1r/ppp2ppp/8/4n3/8/PPP1P1P1/5n1P/RNBK1BNR w KQkq - 1 21";
        Board b = new Board();
        b.loadFromFen(fen);
        Board b2 = b.clone();

        int initialHash = b.getZobristKey();

        assertEquals(b.getZobristKey(), b.hashCode());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.D1, Square.E1));
        b2.doMove(new Move(Square.D1, Square.E1));
        assertEquals(b.getZobristKey(), b.hashCode());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.F2, Square.H1));
        b2.doMove(new Move(Square.F2, Square.H1));

        assertEquals(b.getZobristKey(), b.hashCode());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.F8, Square.C5));
        b2.doMove(new Move(Square.F8, Square.C5));

        assertEquals(b.getZobristKey(), b.hashCode());
        assertEquals(b.hashCode(), b2.hashCode());
        System.out.println(b.getZobristKey());

        b.doMove(new Move(Square.G1, Square.E2));
        b2.doMove(new Move(Square.G1, Square.E2));

        assertEquals(b.getZobristKey(), b.hashCode());
        assertEquals(b.hashCode(), b2.hashCode());
        System.out.println(b.getZobristKey());

        b.doMove(new Move(Square.C1, Square.D2));
        b2.doMove(new Move(Square.C1, Square.D2));

        assertEquals(b.getZobristKey(), b.hashCode());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.C8, Square.E6));
        b2.doMove(new Move(Square.C8, Square.E6));

        assertEquals(b.getZobristKey(), b.hashCode());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.E2, Square.F4));
        b2.doMove(new Move(Square.E2, Square.F4));

        assertEquals(b.getZobristKey(), b.hashCode());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.C3, Square.C4));
        b2.doMove(new Move(Square.C3, Square.C4));

        assertEquals(b.getZobristKey(), b.hashCode());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.A8, Square.D8));
        b2.doMove(new Move(Square.A8, Square.D8));

        assertEquals(b.getZobristKey(), b.hashCode());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.B1, Square.C3));
        b2.doMove(new Move(Square.B1, Square.C3));

        assertEquals(b.getZobristKey(), b.hashCode());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.D8, Square.D6));
        b2.doMove(new Move(Square.D8, Square.D6));

        assertEquals(b.getZobristKey(), b.hashCode());
        assertEquals(b.hashCode(), b2.hashCode());

        for (int i = 1; i <= 11; i++) {
            b.undoMove();
            b2.undoMove();
        }

        assertEquals(initialHash, b.hashCode());
        assertEquals(b.hashCode(), b2.hashCode());
        assertEquals(b, b2);
    }

    @Test
    public void testIncrementalHashKey2() {

        Board b = new Board();
        Board b2 = b.clone();
        int initialHash = b.getZobristKey();

        assertEquals(b.getZobristKey(), b.hashCode());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.E2, Square.E4));
        b2.doMove(new Move(Square.E2, Square.E4));
        assertEquals(b.getZobristKey(), b.hashCode());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.E7, Square.E5));
        b2.doMove(new Move(Square.E7, Square.E5));
        assertEquals(b.getZobristKey(), b.hashCode());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.G1, Square.F3));
        b2.doMove(new Move(Square.G1, Square.F3));
        assertEquals(b.getZobristKey(), b.hashCode());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.B8, Square.C6));
        b2.doMove(new Move(Square.B8, Square.C6));
        assertEquals(b.getZobristKey(), b.hashCode());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.F1, Square.B5));
        b2.doMove(new Move(Square.F1, Square.B5));
        assertEquals(b.getZobristKey(), b.hashCode());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.G8, Square.F6));
        b2.doMove(new Move(Square.G8, Square.F6));
        assertEquals(b.getZobristKey(), b.hashCode());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.E1, Square.G1));
        b2.doMove(new Move(Square.E1, Square.G1));
        assertEquals(b.getZobristKey(), b.hashCode());
        assertEquals(b.hashCode(), b2.hashCode());
        System.out.println(b.getFen());
        for (int i = 1; i <= 7; i++) {
            b.undoMove();
            b2.undoMove();
        }

        assertEquals(initialHash, b.hashCode());
        assertEquals(b.hashCode(), b2.hashCode());

        assertEquals(b, b2);
    }

    @Test
    public void testNullMove() {

        Board b = new Board();
        Board b2 = b.clone();

        b.doNullMove();

        assertNotSame(b.getSideToMove(), b2.getSideToMove());
        assertNotSame(b.hashCode(), b2.hashCode());

        b.undoMove();
        assertEquals(b.getSideToMove(), b2.getSideToMove());
        assertEquals(b.hashCode(), b2.hashCode());

    }

    @Test
    public void testDraws() {

        Board b = new Board();
        b.loadFromFen("rnbqkbnr/p1pppppp/8/8/1p2P3/1P6/P1PP1PPP/RNBQKBNR w KQkq - 0 1");

        b.doMove(new Move(Square.D1, Square.E2));
        assertFalse(b.isDraw());

        b.doMove(new Move(Square.C8, Square.B7));
        assertFalse(b.isDraw());

        b.doMove(new Move(Square.E2, Square.D1));
        assertFalse(b.isDraw());

        b.doMove(new Move(Square.B7, Square.C8));
        assertFalse(b.isDraw());

        b.doMove(new Move(Square.D1, Square.E2));
        assertFalse(b.isDraw());

        b.doMove(new Move(Square.C8, Square.B7));
        assertFalse(b.isDraw());

        b.doMove(new Move(Square.E2, Square.D1));
        assertFalse(b.isDraw());

        b.doMove(new Move(Square.B7, Square.C8));
        assertTrue(b.isDraw());

        b.loadFromFen("1kr5/8/Q7/8/8/7q/4r3/6K1 w - - 0 1");

        b.doMove(new Move(Square.A6, Square.B6));
        assertFalse(b.isDraw());

        b.doMove(new Move(Square.B8, Square.A8));
        assertFalse(b.isDraw());

        b.doMove(new Move(Square.B6, Square.A6));
        assertFalse(b.isDraw());

        b.doMove(new Move(Square.A8, Square.B8));
        assertFalse(b.isDraw());

        b.doMove(new Move(Square.A6, Square.B6));
        assertFalse(b.isDraw());

        b.doMove(new Move(Square.B8, Square.A8));
        assertFalse(b.isDraw());

        b.doMove(new Move(Square.B6, Square.A6));
        assertFalse(b.isDraw());

        b.doMove(new Move(Square.A8, Square.B8));
        assertTrue(b.isDraw());

    }

    @Test
    public void testCastleMove() {

        final Board board = new Board();
        board.loadFromFen("r1bqk1nr/pppp1ppp/2n5/2b1p3/4P3/5N2/PPPPBPPP/RNBQK2R w KQkq - 0 1");
        assertEquals(CastleRight.KING_AND_QUEEN_SIDE, board.getCastleRight(Side.WHITE));
        board.doMove(new Move(Square.E1, Square.G1)); // castle
        final MoveBackup moveBackup = board.getBackup().getLast();
        assertTrue(moveBackup.isCastleMove());
        assertEquals(new Move(Square.H1, Square.F1), moveBackup.getRookCastleMove());
    }

    @Test
    public void testInvalidCastleMove() {

        final Board board = new Board();
        board.loadFromFen("8/5k2/8/8/8/8/5K2/4R3 w - - 0 1");

        final Move whiteRookMoveE1G1 = new Move("e1g1", Side.WHITE);
        board.doMove(whiteRookMoveE1G1);
        final MoveBackup moveBackup = board.getBackup().getLast();
        assertFalse(moveBackup.isCastleMove());
        assertNull(moveBackup.getRookCastleMove());
    }

    @Test
    public void testInsufficientMaterial() {

        final Board board = new Board();
        board.loadFromFen("8/8/8/4k3/8/3K4/8/2BB4 w - - 0 1");
        assertFalse(board.isInsufficientMaterial());
        board.loadFromFen("8/8/8/4k3/5b2/3K4/8/2B5 w - - 0 1");
        assertTrue(board.isInsufficientMaterial());
    }

    @Test
    public void testInsufficientMaterial2() {

        final Board board = new Board();
        final String bishopOnSameColorSquares = "8/8/8/4k3/5b2/3K4/8/2B5 w - - 0 1";
        board.loadFromFen(bishopOnSameColorSquares);
        assertTrue(board.isInsufficientMaterial());
        final String bishopOnDifferentColorSquares = "8/8/8/4k3/5b2/3K4/2B5/8 w - - 0 1";
        board.loadFromFen(bishopOnDifferentColorSquares);
        assertFalse(board.isInsufficientMaterial());
    }

    @Test
    public void testThreefoldRepetition() throws MoveConversionException {
        final MoveList moveList = new MoveList();
        moveList.loadFromSan("1. e4 e5 2. Be2 Be7 3. Bf1 Bf8 4. Bd3 Bd6 5. Bf1 Bf8 6. Bd3 Bd6 7. Bf1 Bf8");

        final Board board = new Board();
        for (Move move : moveList) {
            board.doMove(move);
        }
        assertTrue(board.isRepetition());
    }

    @Test
    public void testThreefoldRepetition1() throws MoveConversionException {
        final MoveList moveList = new MoveList();
        moveList.loadFromSan("1. e4 e5 2. Nf3 Nf6 3. Ng1 Ng8 4. Ke2 Ke7 5. Ke1 Ke8 6. Na3 Na6 7. Nb1 Nb8");

        final Board board = new Board();
        for (Move move : moveList) {
            board.doMove(move);
        }
        assertTrue(board.isRepetition());
    }

    @Test
    public void testThreefoldRepetition2() throws MoveConversionException {

        final MoveList moves = new MoveList();
        moves.loadFromSan("1. e4 e5 2. Nf3 Nf6 3. Ng1 Ng8 4. Ke2 Ke7 5. Ke1 Ke8 6. Na3 Na6 7. Nb1 Nb8");

        final Board board = new Board();
        for (Move move : moves) {
            board.doMove(move);
        }
        assertTrue(board.isRepetition());
    }

    @Test
    public void testThreefoldRepetition3() throws MoveConversionException {

        final MoveList moves = new MoveList();
        moves.loadFromSan("1. Nf3 Nf6 2. Nc3 c5 3. e3 d5 4. Be2 Ne4 5. Bf1 Nf6 6. Be2 Ne4 7. Bf1 Nf6");

        final Board board = new Board();
        for (Move move : moves) {
            board.doMove(move);
        }
        assertTrue(board.isRepetition());
    }

    @Test
    public void testThreefoldRepetition4() throws MoveConversionException {

        final MoveList moves = new MoveList();
        moves.loadFromSan("1. d4 d5 2. Nf3 Nf6 3. c4 e6 4. Bg5 Nbd7 5. e3 Be7 6. Nc3 O-O 7. Rc1 b6 8. cxd5 exd5 9. Qa4 c5 10. Qc6 Rb8 11. Nxd5 Bb7 12. Nxe7+ Qxe7 13. Qa4 Rbc8 14. Qa3 Qe6 15. Bxf6 Qxf6 16. Ba6 Bxf3 17. Bxc8 Rxc8 18. gxf3 Qxf3 19. Rg1 Re8 20. Qd3 g6 21. Kf1 Re4 22. Qd1 Qh3+ 23. Rg2 Nf6 24. Kg1 cxd4 25. Rc4 dxe3 26. Rxe4 Nxe4 27. Qd8+ Kg7 28. Qd4+ Nf6 29. fxe3 Qe6 30. Rf2 g5 31. h4 gxh4 32. Qxh4 Ng4 33. Qg5+ Kf8 34. Rf5 h5 35. Qd8+ Kg7 36. Qg5+ Kf8 37. Qd8+ Kg7 38. Qg5+ Kf8");

        final Board board = new Board();
        System.out.println(board.hashCode());
        for (Move move : moves) {
            board.doMove(move);
        }
        assertTrue(board.isRepetition());
    }

    @Test
    public void testThreefoldRepetition5() throws MoveConversionException {

        // en passant capture not possible for would expose own king to check
        final Board board = new Board();
        board.loadFromFen("6k1/8/8/8/6p1/8/5PR1/6K1 w - - 0 32");

        board.doMove(new Move(Square.F2, Square.F4)); // initial position - two square pawn advance
        board.doMove(new Move(Square.G8, Square.F7)); // en passant capture not possible - would expose own king to check
        board.doMove(new Move(Square.G1, Square.F2));
        board.doMove(new Move(Square.F7, Square.G8));

        board.doMove(new Move(Square.F2, Square.G1)); // twofold repetition
        board.doMove(new Move(Square.G8, Square.H7));

        board.doMove(new Move(Square.G1, Square.H2));
        board.doMove(new Move(Square.H7, Square.G8));

        board.doMove(new Move(Square.H2, Square.G1)); // threefold repetiton
        assertTrue(board.isRepetition());
    }

    @Test
    public void testThreefoldRepetition6() throws MoveConversionException {

        // en passant capture not possible for own king in check
        final Board board = new Board();
        board.loadFromFen("8/8/8/8/4p3/8/R2P3k/K7 w - - 0 37");

        board.doMove(new Move(Square.D2, Square.D4)); // initial position - two square pawn advance
        board.doMove(new Move(Square.H2, Square.H3)); // en passant capture not possible - own king in check

        board.doMove(new Move(Square.A2, Square.A3));
        board.doMove(new Move(Square.H3, Square.H2));

        board.doMove(new Move(Square.A3, Square.A2)); // twofold repetition
        board.doMove(new Move(Square.H2, Square.H1));

        board.doMove(new Move(Square.A2, Square.A3));
        board.doMove(new Move(Square.H1, Square.H2));

        board.doMove(new Move(Square.A3, Square.A2)); // threefold repetiton
        assertTrue(board.isRepetition());

    }

    @Test
    public void testThreefoldRepetition7() throws MoveConversionException {

        final MoveList moves = new MoveList();
        moves.loadFromSan("1. e4 Nf6 2. e5 d5 3. Bc4 Nc6 4. Bf1 Nb8 5. Bc4 Nc6 6. Bf1 Nb8");

        final Board board = new Board();
        for (Move move : moves) {
            board.doMove(move);
        }
        assertFalse(board.isRepetition());
    }
}