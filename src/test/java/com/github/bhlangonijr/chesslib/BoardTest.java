package com.github.bhlangonijr.chesslib;

import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import com.github.bhlangonijr.chesslib.move.MoveList;
import org.junit.Test;

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

        MoveList moves = MoveGenerator.generateLegalMoves(b1);

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

        MoveList moves = MoveGenerator.generateLegalMoves(b);

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

        MoveList moves = MoveGenerator.generateLegalMoves(b);

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

        MoveList moves = MoveGenerator.generateLegalMoves(b);

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
}
