package com.github.bhlangonijr.chesslib;

import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import com.github.bhlangonijr.chesslib.move.MoveList;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BoardTest {

    @Test
    public void testMoveAndFENParsing() {

        String fen1 = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
        String fen2 = "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2";
        String fen3 = "rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 2";
        String fen4 = "rnbqkbnr/ppp1pppp/8/3pP3/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 3";
        String fen5 = "rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 4";
        String fen6 = "rnbqkbnr/ppp1p1pp/5P2/3p4/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 5";
        String fen7 = "rnbqkbnr/ppp3pp/5p2/3p4/8/8/PPPP1PPP/RNBQKBNR w KQkq - 0 6";

        Board board = new Board();

        board.loadFromFEN(fen1);

        assertEquals(Piece.BLACK_BISHOP, board.getPiece(Square.C8));
        assertEquals(Piece.WHITE_BISHOP, board.getPiece(Square.C1));
        assertEquals(Piece.BLACK_ROOK, board.getPiece(Square.H8));
        assertEquals(Piece.WHITE_ROOK, board.getPiece(Square.H1));

        assertEquals(new Integer(0), board.getHalfMoveCounter());
        assertEquals(new Integer(1), board.getMoveCounter());
        assertEquals(Square.E3, board.getEnPassant());

        assertEquals(fen1, board.getFEN());

        Move move = new Move(Square.E7, Square.E5); //sm: b
        board.doMove(move, true);
        System.out.println("new FEN after: " + move.toString() + ": " + board.getFEN());

        assertEquals(fen2, board.getFEN()); //sm: w

        board.undoMove();
        System.out.println("new FEN after: undo: " + board.getFEN());
        assertEquals(fen1, board.getFEN()); // sm: b

        move = new Move(Square.D7, Square.D5); //sm: b
        board.doMove(move, true);
        System.out.println("new FEN after: " + move.toString() + ": " + board.getFEN());
        assertEquals(fen3, board.getFEN()); // sm: w

        move = new Move(Square.E4, Square.E5); //sm: w
        board.doMove(move, true);
        System.out.println("new FEN after: " + move.toString() + ": " + board.getFEN());
        assertEquals(fen4, board.getFEN()); // sm: b

        move = new Move(Square.F7, Square.F5); //sm: b
        board.doMove(move, true);
        System.out.println("new FEN after: " + move.toString() + ": " + board.getFEN());
        assertEquals(fen5, board.getFEN()); // sm: w

        move = new Move(Square.E5, Square.F6); //sm: w
        board.doMove(move, true);
        System.out.println("new FEN after: " + move.toString() + ": " + board.getFEN());
        assertEquals(fen6, board.getFEN()); // sm: b

        move = new Move(Square.E7, Square.F6); //sm: b
        board.doMove(move, true);
        System.out.println("new FEN after: " + move.toString() + ": " + board.getFEN());
        assertEquals(fen7, board.getFEN()); // sm: w

    }

    @Test
    public void testCastleAndFENParsing() {

        String fen1 = "rnbqk2r/ppp1b1pp/5p1n/3p4/8/3B1N2/PPPP1PPP/RNBQK2R w KQkq - 4 3";
        String fen2 = "rnbqk2r/ppp1b1pp/5p1n/3p4/8/3B1N2/PPPP1PPP/RNBQ1RK1 b kq - 5 4";
        String fen3 = "rnbq1rk1/ppp1b1pp/5p1n/3p4/8/3B1N2/PPPP1PPP/RNBQ1RK1 w - - 6 5";

        Board board = new Board();

        board.loadFromFEN(fen1);

        assertEquals(fen1, board.getFEN());

        Move move = new Move(Square.E1, Square.G1); //sm: b
        board.doMove(move, true);
        System.out.println("new FEN after: " + move.toString() + ": " + board.getFEN());

        assertEquals(fen2, board.getFEN()); //sm: w

        move = new Move(Square.E8, Square.G8); //sm: w
        board.doMove(move, true);
        System.out.println("new FEN after: " + move.toString() + ": " + board.getFEN());

        assertEquals(fen3, board.getFEN()); //sm: b

        board.undoMove();
        System.out.println("new FEN after: undo: " + board.getFEN());
        assertEquals(fen2, board.getFEN()); // sm: w

    }

    @Test
    public void testClone()  {
        String fen1 = "rnbqk2r/ppp1b1pp/5p1n/3p4/8/3B1N2/PPPP1PPP/RNBQK2R w KQkq - 4 3";
        Board b1 = new Board();
        b1.loadFromFEN(fen1);

        Board b2 = b1.clone();

        assertEquals(b1.hashCode(), b2.hashCode());

    }

    @Test
    public void testEquality() {
        String fen1 = "rnbqk2r/ppp1b1pp/5p1n/3p4/8/3B1N2/PPPP1PPP/RNBQK2R w KQkq - 4 3";
        Board b1 = new Board();
        b1.loadFromFEN(fen1);

        Board b2 = new Board();
        b2.loadFromFEN(fen1);

        assertEquals(b1, b2);
        assertEquals(b1.getPositionId(), b2.getPositionId());

    }

    @Test
    public void testUndoMove() {
        String fen1 = "rnbqkbnr/1p1ppppp/p7/1Pp5/8/8/P1PPPPPP/RNBQKBNR w KQkq c6 0 5";
        Board b1 = new Board();
        b1.loadFromFEN(fen1);

        b1.doMove(new Move(Square.B5, Square.A6));
        b1.undoMove();

        assertEquals(fen1, b1.getFEN());

    }

    @Test
    public void testLegalMove() throws MoveGeneratorException {
        String fen1 = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/5Q2/PPPBBPpP/RN2K2R w KQkq - 0 2";
        Board b1 = new Board();
        b1.loadFromFEN(fen1);

        MoveList moves = MoveGenerator.getInstance().generateLegalMoves(b1);

        assertEquals(47, moves.size());
    }

}
