package com.github.bhlangonijr.chesslib; 

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.Square;


/** 
 * The below test positions are from the official test data.
 * https://github.com/ddugovic/polyglot/blob/master/book_format.html
 */

public class PolyglotKeyTest {

    @Test
    public void testStartingPosition() {
        Board board = new Board();
        assertEquals(0x463b96181691fc9cL, board.getPolyglotKey());
    }

    @Test
    public void testAfterE2E4() {
        Board board = new Board();
        board.loadFromFen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
        assertEquals(0x823c9b50fd114196L, board.getPolyglotKey());
    }

    @Test
    public void testAfterE2E4D7D5() {
        Board board = new Board();
        board.loadFromFen("rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 2");
        assertEquals(0x0756b94461c50fb0L, board.getPolyglotKey());
    }

    @Test
    public void testAfterE2E4D7D5E4E5() {
        Board board = new Board();
        board.loadFromFen("rnbqkbnr/ppp1pppp/8/3pP3/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 2");
        assertEquals(0x662fafb965db29d4L, board.getPolyglotKey());
    }

    @Test
    public void testAfterE2E4D7D5E4E5F7F5() {
        Board board = new Board();
        board.loadFromFen("rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3");
        assertEquals(0x22a48b5a8e47ff78L, board.getPolyglotKey());
    }

    @Test
    public void testAfterE2E4D7D5E4E5F7F5E1E2() {
        Board board = new Board();
        board.loadFromFen("rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPPKPPP/RNBQ1BNR b kq - 0 3");
        assertEquals(0x652a607ca3f242c1L, board.getPolyglotKey());
    }

    @Test
    public void testAfterE2E4D7D5E4E5F7F5E1E2E8F7() {
        Board board = new Board();
        board.loadFromFen("rnbq1bnr/ppp1pkpp/8/3pPp2/8/8/PPPPKPPP/RNBQ1BNR w - - 0 4");
        assertEquals(0x00fdd303c946bdd9L, board.getPolyglotKey());
    }

    @Test
    public void testAfterA2A4B7B5H2H4B5B4C2C4() {
        Board board = new Board();
        board.loadFromFen("rnbqkbnr/p1pppppp/8/8/PpP4P/8/1P1PPPP1/RNBQKBNR b KQkq c3 0 3");
        assertEquals(0x3c8123ea7b067637L, board.getPolyglotKey());
    }

    @Test
    public void testAfterA2A4B7B5H2H4B5B4C2C4B4C3A1A3() {
        Board board = new Board();
        board.loadFromFen("rnbqkbnr/p1pppppp/8/8/P6P/R1p5/1P1PPPP1/1NBQKBNR b Kkq - 0 4");
        assertEquals(0x5c3f9b829b279560L, board.getPolyglotKey());
    }

    @Test
    public void testKeyRestoredAfterUndoMove() {
        Board board = new Board();
        long originalKey = board.getPolyglotKey();

        Move move = new Move(Square.E2, Square.E4);
        board.doMove(move);
        assertEquals(0x823c9b50fd114196L, board.getPolyglotKey()); 

        board.undoMove();
        assertEquals(originalKey, board.getPolyglotKey());
    }

    @Test
    public void testKeyRestoredAfterMultipleDoUndoMoves() {
        Board board = new Board();
        long originalKey = board.getPolyglotKey();

        board.doMove(new Move(Square.E2, Square.E4));
        board.doMove(new Move(Square.D7, Square.D5));
        board.doMove(new Move(Square.E4, Square.E5));

        board.undoMove();
        board.undoMove();
        board.undoMove();

        assertEquals(originalKey, board.getPolyglotKey());
    }
}

