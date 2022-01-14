package com.github.bhlangonijr.chesslib.unicode;

import java.io.PrintStream;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;

/**
 * A printer class for conveniently printing boards using Unicode chess symbols in a reliable and consistent way.
 */
public class UnicodePrinter {
    private final PrintStream printStream;

    /**
     * Construct a printer using the specified print stream.
     * 
     * @param printStream the print stream where to output the board
     */
    public UnicodePrinter(PrintStream printStream) {
        this.printStream = printStream;
    }

    /**
     * Construct a printer using {@code System.out} as a print stream.
     * <p>
     * Same as invoking {@code new UnicodePrinter(System.out)}.
     *
     * @see UnicodePrinter#UnicodePrinter(PrintStream)
     */
    public UnicodePrinter() {
        this(System.out);
    }

    /**
     * Prints the board using Unicode chess symbols.
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
