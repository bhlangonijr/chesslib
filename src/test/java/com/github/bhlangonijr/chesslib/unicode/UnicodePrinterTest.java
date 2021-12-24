package com.github.bhlangonijr.chesslib.unicode;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

import org.junit.Test;

public class UnicodePrinterTest {
    @Test
    public void testPrintBoard() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(256);
        UnicodePrinter printer = new UnicodePrinter(new PrintStream(baos));

        Board board = new Board();
        board.doMove(new Move(Square.E2, Square.E4));
        board.doMove(new Move(Square.E7, Square.E5));
        printer.print(board);

        String repr = baos.toString(StandardCharsets.UTF_8);
        assertEquals("Should be a white rook", '\u2656', repr.charAt(0));
        assertEquals("Should be a black rook", '\u265C', repr.charAt(63));
    }
}
