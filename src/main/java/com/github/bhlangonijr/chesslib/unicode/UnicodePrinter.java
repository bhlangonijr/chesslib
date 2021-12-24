package com.github.bhlangonijr.chesslib.unicode;

import java.io.PrintStream;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;

/**
 * Printer class for printing boards using Unicode chess sybols.
 */
public class UnicodePrinter {
    private PrintStream printStream;

    /**
     * Construct a printer using your choice of PrintStream
     * 
     * @param printStream the PrintStream to which the board represenation will be sent
     */
    public UnicodePrinter(PrintStream printStream) {
        this.printStream = printStream;
    }

    /**
     * Construct a printer using System.out
     */
    public UnicodePrinter() {
        this(System.out);
    }

    /**
     * Print the board using Unicode chess symbols
     * 
     * @param board the board to print
     */
    public void print(Board board) {
        int row = 0;
        for (Piece p : board.boardToArray()) {
            if (p == Piece.NONE) {
                printStream.print(' ');
            } else {
                printStream.print(p.getFanSymbol());
            }
            if (++row % 8 == 0) {
                printStream.println();
            }
        }
    }
}
