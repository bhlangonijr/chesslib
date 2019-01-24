package com.github.bhlangonijr.chesslib;

import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import com.github.bhlangonijr.chesslib.move.MoveList;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

        assertEquals(fen2, board.getFen()); //sm: w

        board.undoMove();
        System.out.println("new FEN after: undo: " + board.getFen());
        assertEquals(fen1, board.getFen()); // sm: b

        move = new Move(Square.D7, Square.D5); //sm: b
        board.doMove(move, true);
        System.out.println("new FEN after: " + move.toString() + ": " + board.getFen());
        assertEquals(fen3, board.getFen()); // sm: w

        move = new Move(Square.E4, Square.E5); //sm: w
        board.doMove(move, true);
        System.out.println("new FEN after: " + move.toString() + ": " + board.getFen());
        assertEquals(fen4, board.getFen()); // sm: b

        move = new Move(Square.F7, Square.F5); //sm: b
        board.doMove(move, true);
        System.out.println("new FEN after: " + move.toString() + ": " + board.getFen());
        assertEquals(fen5, board.getFen()); // sm: w

        move = new Move(Square.E5, Square.F6); //sm: w
        board.doMove(move, true);
        System.out.println("new FEN after: " + move.toString() + ": " + board.getFen());
        assertEquals(fen6, board.getFen()); // sm: b

        move = new Move(Square.E7, Square.F6); //sm: b
        board.doMove(move, true);
        System.out.println("new FEN after: " + move.toString() + ": " + board.getFen());
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

        assertEquals(fen2, board.getFen()); //sm: w

        move = new Move(Square.E8, Square.G8); //sm: w
        board.doMove(move, true);
        System.out.println("new FEN after: " + move.toString() + ": " + board.getFen());

        assertEquals(fen3, board.getFen()); //sm: b

        board.undoMove();
        System.out.println("new FEN after: undo: " + board.getFen());
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

        Board b2 = new Board();
        b2.loadFromFen(fen1);

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
}
