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

        assertEquals(Integer.valueOf(0), board.getHalfMoveCounter());
        assertEquals(Integer.valueOf(1), board.getMoveCounter());
        assertEquals(Square.E3, board.getEnPassant());
        assertEquals(Square.NONE, board.getEnPassantTarget());

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

        long initialHash = b.getZobristKey();

        assertEquals(b.getZobristKey(), b.getIncrementalHashKey());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.D1, Square.E1));
        b2.doMove(new Move(Square.D1, Square.E1));
        assertEquals(b.getZobristKey(), b.getIncrementalHashKey());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.F2, Square.H1));
        b2.doMove(new Move(Square.F2, Square.H1));

        assertEquals(b.getZobristKey(), b.getIncrementalHashKey());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.F8, Square.C5));
        b2.doMove(new Move(Square.F8, Square.C5));

        assertEquals(b.getZobristKey(), b.getIncrementalHashKey());
        assertEquals(b.hashCode(), b2.hashCode());
        System.out.println(b.getZobristKey());

        b.doMove(new Move(Square.G1, Square.E2));
        b2.doMove(new Move(Square.G1, Square.E2));

        assertEquals(b.getZobristKey(), b.getIncrementalHashKey());
        assertEquals(b.hashCode(), b2.hashCode());
        System.out.println(b.getZobristKey());

        b.doMove(new Move(Square.C1, Square.D2));
        b2.doMove(new Move(Square.C1, Square.D2));

        assertEquals(b.getZobristKey(), b.getIncrementalHashKey());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.C8, Square.E6));
        b2.doMove(new Move(Square.C8, Square.E6));

        assertEquals(b.getZobristKey(), b.getIncrementalHashKey());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.E2, Square.F4));
        b2.doMove(new Move(Square.E2, Square.F4));

        assertEquals(b.getZobristKey(), b.getIncrementalHashKey());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.C3, Square.C4));
        b2.doMove(new Move(Square.C3, Square.C4));

        assertEquals(b.getZobristKey(), b.getIncrementalHashKey());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.A8, Square.D8));
        b2.doMove(new Move(Square.A8, Square.D8));

        assertEquals(b.getZobristKey(), b.getIncrementalHashKey());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.B1, Square.C3));
        b2.doMove(new Move(Square.B1, Square.C3));

        assertEquals(b.getZobristKey(), b.getIncrementalHashKey());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.D8, Square.D6));
        b2.doMove(new Move(Square.D8, Square.D6));

        assertEquals(b.getZobristKey(), b.getIncrementalHashKey());
        assertEquals(b.hashCode(), b2.hashCode());

        for (int i = 1; i <= 11; i++) {
            b.undoMove();
            b2.undoMove();
        }

        assertEquals(initialHash, b.getZobristKey());
        assertEquals(b.hashCode(), b2.hashCode());
        assertEquals(b, b2);
    }

    @Test
    public void testIncrementalHashKey2() {

        Board b = new Board();
        Board b2 = b.clone();
        long initialHash = b.getZobristKey();

        assertEquals(b.getZobristKey(), b.getIncrementalHashKey());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.E2, Square.E4));
        b2.doMove(new Move(Square.E2, Square.E4));
        assertEquals(b.getZobristKey(), b.getIncrementalHashKey());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.E7, Square.E5));
        b2.doMove(new Move(Square.E7, Square.E5));
        assertEquals(b.getZobristKey(), b.getIncrementalHashKey());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.G1, Square.F3));
        b2.doMove(new Move(Square.G1, Square.F3));
        assertEquals(b.getZobristKey(), b.getIncrementalHashKey());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.B8, Square.C6));
        b2.doMove(new Move(Square.B8, Square.C6));
        assertEquals(b.getZobristKey(), b.getIncrementalHashKey());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.F1, Square.B5));
        b2.doMove(new Move(Square.F1, Square.B5));
        assertEquals(b.getZobristKey(), b.getIncrementalHashKey());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.G8, Square.F6));
        b2.doMove(new Move(Square.G8, Square.F6));
        assertEquals(b.getZobristKey(), b.getIncrementalHashKey());
        assertEquals(b.hashCode(), b2.hashCode());

        b.doMove(new Move(Square.E1, Square.G1));
        b2.doMove(new Move(Square.E1, Square.G1));
        assertEquals(b.getZobristKey(), b.getIncrementalHashKey());
        assertEquals(b.hashCode(), b2.hashCode());
        System.out.println(b.getFen());
        for (int i = 1; i <= 7; i++) {
            b.undoMove();
            b2.undoMove();
        }

        assertEquals(initialHash, b.getZobristKey());
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
    public void testInsufficientMaterial1() {

        final Board board = new Board();
        board.loadFromFen("B3k3/8/8/8/8/8/8/4KB2 w - - 0 1");
        assertTrue(board.isInsufficientMaterial());
        board.loadFromFen("B1b1k3/3b4/4b3/8/8/8/8/4KB2 w - - 0 1");
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
	public void testInsufficientMaterialEssentialCombinations() {

		final Board board = new Board();

		board.loadFromFen("6k1/8/3K4/8/8/8/3BB3/8 b - - 0 33"); // KBwBbvK
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/6k1/3K4/4N3/8/7B/8/8 w - - 0 36"); // KBwNvK
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/6k1/3K4/8/8/5N2/8/2B5 b - - 0 33"); // KBbNvK
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/8/3K2k1/8/8/8/8/5B2 b - - 0 33"); // KBwvK
		assertTrue(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/8/8/4k3/8/4K3/8/2B5 w - - 0 35"); // KBbvK
		assertTrue(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/8/3b2k1/8/3K2B1/8/8/8 b - - 0 35"); // KBwvKBb
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/8/k3b3/8/5K2/8/8/2B5 b - - 0 35"); // KBbvKBw
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("5K2/8/8/1B6/8/k7/6b1/8 w - - 0 39"); // KBwvKBw
		assertTrue(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/8/8/4k3/5b2/3K4/8/2B5 w - - 0 33"); // KBbvKBb
		assertTrue(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/8/4K3/8/1n6/8/5k1N/8 w - - 0 37"); // KNvKN
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("5k2/8/5N2/2K5/8/5N2/8/8 w - - 0 34"); // KNNvK
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("4k3/8/8/2K5/8/5N2/8/8 w - - 0 35"); // KNvK
		assertTrue(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/5k2/8/2K5/8/8/8/8 w - - 0 37"); // KvK
		assertTrue(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("2b5/8/2k5/8/4K3/8/8/8 b - - 0 34"); // KvKBw
		assertTrue(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/8/8/2b5/2K5/8/4k3/8 w - - 0 34"); // KvKBb
		assertTrue(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("4k3/8/8/4K3/8/b7/4b3/8 b - - 0 34"); // KvKBwBb
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("4k3/8/8/2K1n3/8/8/4b3/8 b - - 0 35"); // KvKBwN
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("1n2k3/8/8/8/8/b4K2/8/8 b - - 0 33"); // KvKBbN
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("4k3/8/8/1K2n3/8/8/8/8 b - - 0 36"); // KvKN
		assertTrue(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("1n2k3/8/3n4/8/8/8/3K4/8 b - - 0 35"); // KvKNN
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("2B5/3B4/8/6K1/8/1k6/8/8 w - - 0 32"); // KBwBwvK
		assertTrue(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("5B2/8/2k5/8/8/1K6/8/2B5 b - - 0 36"); // KBbBbvK
		assertTrue(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("3k4/1b6/2b5/8/8/8/3K4/8 w - - 0 27"); // KvKBwBw
		assertTrue(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("5b2/3k4/8/8/5K2/2b5/8/8 b - - 0 25"); // KvKBbBb
		assertTrue(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("7r/3k4/8/8/8/8/3K4/1N6 w - - 0 21"); // KNvKR
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/k2b4/8/8/2K5/8/8/1N6 w - - 0 23"); // KNvKBw
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("5b2/8/3k4/8/8/4K3/8/1N6 w - - 0 24"); // KNvKBb
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("4k3/8/8/8/8/5K2/3q4/1N6 w - - 0 24"); // KNvKQ
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("3k4/8/3p4/8/8/4K3/8/1N6 w - - 0 23"); // KNvKP
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("r7/8/3K4/8/8/2k5/8/5B2 w - - 0 28"); // KBwvKR
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("1n6/8/8/2k1K3/8/8/8/5B2 w - - 0 26"); // KBwvKN
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("3K4/8/8/4q3/k7/8/8/5B2 b - - 0 30"); // KBwvKQ
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/8/8/2p1K3/8/1k6/8/5B2 w - - 0 27"); // KBwvKP
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/8/8/r7/k7/8/1K6/2B5 b - - 0 25"); // KBbvKR
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("1n6/8/8/8/4K3/1k6/1B6/8 b - - 3 27"); // KBbvKN
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/8/8/2q5/8/2BK4/k7/8 b - - 0 28"); // KBbvKQ
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/6B1/8/2p5/8/1k6/8/1K6 b - - 0 26"); // KBbvKP
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("1n6/8/8/2k5/4K3/8/7R/8 b - - 0 22"); // KRvKN
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("1n6/8/8/4Q3/k3K3/8/8/8 b - - 0 24"); // KQvKN
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("1n6/7Q/8/2Q5/8/k2K4/8/8 b - - 0 28"); // KQQvKN
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("1n6/8/8/2k5/8/8/6KP/8 b - - 0 24"); // KPvKN
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("2b5/8/8/8/2k5/8/8/2K4R w - - 0 24"); // KRvKBw
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("2b5/8/8/1k6/3R4/8/4K3/1b6 b - - 0 26"); // KRvKBwBw
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("2b5/2Q5/8/8/8/k7/8/2K5 w - - 0 25"); // KQvKBw
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("4B3/8/1k6/8/4b3/2K5/7Q/8 w - - 0 30"); // KQBwvKBw
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("3Q4/2Q5/b7/8/k7/8/8/2K5 b - - 0 26"); // KQQvKBw
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("2b5/8/8/8/k7/8/K7/3b3Q b - - 0 27"); // KQvKBwBw
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("1Qb5/7Q/8/8/k7/2K5/8/3b4 b - - 0 30"); // KQQvKBwBw
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/8/8/3P4/k5b1/8/8/2K5 w - - 0 27"); // KPvKBw
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/5kb1/8/8/8/3K4/8/7R w - - 0 24"); // KRvKBb
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("5b2/8/6k1/8/8/R1K5/8/4b3 w - - 0 29"); // KRvKBbBb
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/8/7k/4Q3/8/b2K4/8/8 w - - 0 24"); // KQvKBb
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("5B2/3Q4/7b/3K3k/8/8/8/8 b - - 0 29"); // KQBbvKBb
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("4Qb2/6k1/8/8/Q7/3K4/8/8 w - - 0 29"); // KQQvKBb
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("5b2/7k/8/8/K7/1Q6/8/4b3 w - - 0 31"); // KQvKBbBb
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("5b2/Q7/8/8/7k/1K6/3Q4/4b3 w - - 0 34"); // KQQvKBbBb
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("5b2/8/4P3/3K2k1/8/8/8/8 b - - 0 25"); // KPvKBb
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/1b2q3/8/k4K2/8/8/8/5B2 w - - 0 25"); // KBwvKBwQ
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("r1b5/8/8/8/1k6/8/3K4/5B2 b - - 0 24"); // KBwvKBwR
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("r7/3B4/8/8/1k3q2/8/8/1K6 b - - 0 24"); // KBwvKQR
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("r1b5/8/8/8/1k3q2/8/8/1K3B2 w - - 0 23"); // KBwvKBwQR
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("4kb2/6q1/8/8/8/8/3K4/2B5 w - - 0 28"); // KBbvKBbQ
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("4kb2/8/8/8/4r3/8/3K4/2B5 w - - 0 28"); // KBbvKBbR
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/3k3r/8/8/5q2/8/3K4/2B5 w - - 0 28"); // KBbvKQR
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("4kb2/7r/8/3q4/8/8/3K4/2B5 w - - 0 27"); // KBbvKBbQR
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("5b2/5k2/8/4q3/2K5/8/8/2B5 b - - 1 31"); // KBbvKQBb
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/8/4k3/7q/8/K7/3B1q2/8 b - - 0 32"); // KBbvKQQ
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("7B/4k3/8/8/8/1q6/3BK3/8 b - - 0 34"); // KBbBbvKQ
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/3q4/8/8/k2B4/B5K1/3q4/8 b - - 0 29"); // KBbBbvKQQ
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/8/7B/k7/8/8/1B4r1/4K3 b - - 1 32"); // KBbBbvKR
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("Q7/8/8/1k6/8/8/3K4/b1B5 w - - 0 28"); // KBbQvKBb
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("3R3Q/8/7b/8/k7/8/4K3/2B5 b - - 0 31"); // KBbQRvKBb
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("3R1b2/8/8/8/k7/8/4K3/2B5 w - - 0 33"); // KBbRvKBb
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/8/8/8/5q2/1k1B4/8/1b1K4 w - - 0 31"); // KBwvKQBw
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("1q6/8/8/8/5q2/1k6/6K1/5B2 w - - 0 29"); // KBwvKQQ
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("q3B3/8/8/8/8/1k4K1/8/5B2 b - - 0 34"); // KBwBwvKQ
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("q1B5/8/4B1q1/8/1k6/8/8/5K2 w - - 0 32"); // KBwBwvKQQ
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("r1B5/8/8/8/8/2k5/8/3K1B2 b - - 0 32"); // KBwBwvKR
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/8/k7/8/6B1/5b2/3Q4/2K5 w - - 0 35"); // KBwQvKBw
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("5R1Q/1b1B4/8/8/2k5/8/8/2K5 b - - 0 38"); // KBwQRvKBw
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("1R6/8/3k4/8/5K2/7B/4b3/8 w - - 0 30"); // KBwRvKBw
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("8/8/3k4/6q1/8/5K2/8/1Nq5 w - - 0 27"); // KNvKQQ
		assertFalse(board.isInsufficientMaterial());
		assertTrue(board.isInsufficientMaterial(Side.WHITE));
		assertFalse(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("1R6/3k4/8/b7/8/8/5K2/1Q6 w - - 0 30"); // KQRvKBb
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

		board.loadFromFen("1R6/8/4k3/8/8/3b4/3Q4/4K3 w - - 0 27"); // KQRvKBw
		assertFalse(board.isInsufficientMaterial());
		assertFalse(board.isInsufficientMaterial(Side.WHITE));
		assertTrue(board.isInsufficientMaterial(Side.BLACK));

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
        assertFalse(board.isRepetition());
    }

    @Test
    public void testThreefoldRepetition2() throws MoveConversionException {

        final MoveList moves = new MoveList();
        moves.loadFromSan("1. Nf3 Nf6 2. c4 c5 3. b3 d6 4. d4 cxd4 5. Nxd4 e5 6. Nb5 Be6 7. g3 a6 8. N5c3 d5 9. cxd5 Nxd5 10. Bg2 Bb4 11. Bd2 Nc6 12. O-O O-O 13. Na4 Rc8 14. a3 Be7 15. e3 b5 16. Nb2 Qb6 17. Nd3 Rfd8 18. Qe2 Nf6 19. Nc1 e4 20. Bc3 Nd5 21. Bxe4 Nxc3 22. Nxc3 Na5 23. N1a2 Nxb3 24. Rad1 Bc4 25. Qf3 Qf6 26. Qg4 Be6 27. Qe2 Rxc3 28. Nxc3 Qxc3 29. Rxd8+ Bxd8 30. Rd1 Be7 31. Bb7 Nc5 32. Qf3 g6 33. Bd5 Bxd5 34. Qxd5 Qxa3 35. Qe5 Ne6 36. Ra1 Qd6 37. Qxd6 Bxd6 38. Rxa6 Bc5 39. Kf1 Kf8 40. Ke2 Ke7 41. Kd3 Kd7 42. g4 Kc7 43. Ra8 Kc6 44. f4 Be7 45. Rc8+ Kd5 46. Re8 Kd6 47. g5 f5 48. Rb8 Kc6 49. Re8 Kd6 50. Rb8 Kc6 51. Re8 Kd6");

        final Board board = new Board();
        for (Move move : moves) {
            board.doMove(move);
            System.out.println(board.getZobristKey() + "\t = " + move + "\n = " + board.getFen());
        }
        assertFalse(board.isRepetition());
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

    @Test
    public void testBoardToString() throws MoveConversionException {

        // Creates a new chessboard in the standard initial position
        Board board = new Board();

        board.doMove(new Move(Square.E2, Square.E4));
        board.doMove(new Move(Square.B8, Square.C6));
        board.doMove(new Move(Square.F1, Square.C4));

        //print the chessboard in a human-readable form
        System.out.println(board.toString());

        final String expected =
                "r.bqkbnr\n" +
                "pppppppp\n" +
                "..n.....\n" +
                "........\n" +
                "..B.P...\n" +
                "........\n" +
                "PPPP.PPP\n" +
                "RNBQK.NR\n" +
                "Side: BLACK";
        assertEquals(expected, board.toString());
    }

    @Test
    public void testToStringFromWhiteViewPoint() throws MoveConversionException {

        // Creates a new chessboard in the standard initial position
        Board board = new Board();

        board.doMove("e4");
        board.doMove("Nc6");
        board.doMove("Bc4");

        final String expected =
                "r.bqkbnr\n" +
                "pppppppp\n" +
                "..n.....\n" +
                "........\n" +
                "..B.P...\n" +
                "........\n" +
                "PPPP.PPP\n" +
                "RNBQK.NR\n";
        assertEquals(expected, board.toStringFromWhiteViewPoint());
        assertEquals(expected, board.toStringFromViewPoint(Side.WHITE));
    }

    @Test
    public void testToStringFromBlackViewPoint() throws MoveConversionException {

        // Creates a new chessboard in the standard initial position
        Board board = new Board();

        board.doMove("e4");
        board.doMove("Nc6");
        board.doMove("Bc4");

        final String expected =
                "RN.KQBNR\n" +
                "PPP.PPPP\n" +
                "........\n" +
                "...P.B..\n" +
                "........\n" +
                ".....n..\n" +
                "pppppppp\n" +
                "rnbkqb.r\n";
        assertEquals(expected, board.toStringFromBlackViewPoint());
        assertEquals(expected, board.toStringFromViewPoint(Side.BLACK));
    }

    @Test
    public void testBoardStrictEquals() throws MoveConversionException {

        Board board = new Board();

        board.doMove(new Move(Square.E2, Square.E4));
        board.doMove(new Move(Square.E7, Square.E5));

        Board board2 = board.clone();

        assertEquals(board, board2);
        assertTrue(board.strictEquals(board2));

        Board board3 = new Board();
        board3.loadFromFen(board.getFen());

        assertEquals(board, board3);
        assertFalse(board.strictEquals(board3));

    }

    @Test
    public void testBoardConsistencyAfterUndoingMove() throws MoveConversionException {

        final Board board = new Board();
        final Move e2e4 = new Move(Square.E2, Square.E4);
        final Move e7e5 = new Move(Square.E7, Square.E5);
        board.doMove(e2e4);
        board.doMove(e7e5);
        long initialKey = board.getIncrementalHashKey();

        board.undoMove();
        board.doMove(e7e5);
        assertEquals(initialKey, board.getIncrementalHashKey());
        assertEquals((long) board.getHistory().getLast(), board.getIncrementalHashKey());
        assertEquals(board.getZobristKey(), initialKey);

    }

    @Test
    public void testDoSanMove() {

        Board board = new Board();
        board.loadFromFen("4k3/8/8/8/1b6/2N5/8/4K1N1 w - - 0 1");
        board.doMove("Ne2");
        assertEquals("4k3/8/8/8/1b6/2N5/4N3/4K3 b - - 1 1", board.getFen());
        board.doMove("Bxc3");
        assertEquals("4k3/8/8/8/8/2b5/4N3/4K3 w - - 0 2", board.getFen());
        board.doMove("Nxc3");
        assertEquals("4k3/8/8/8/8/2N5/8/4K3 b - - 0 2", board.getFen());
    }

    @Test
    public void testDoSanMove2() {

        Board board = new Board();
        board.doMove("e4");
        assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", board.getFen());
    }


}