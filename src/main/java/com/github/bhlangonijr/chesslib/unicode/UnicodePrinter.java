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
            switch (p) {
                case BLACK_BISHOP:
                    printStream.print('\u265D');
                    break;
                case BLACK_KING:
                    printStream.print('\u265A');
                    break;
                case BLACK_KNIGHT:
                    printStream.print('\u265E');
                    break;
                case BLACK_PAWN:
                    printStream.print('\u265F');
                    break;
                case BLACK_QUEEN:
                    printStream.print('\u265B');
                    break;
                case BLACK_ROOK:
                    printStream.print('\u265C');
                    break;
                case WHITE_BISHOP:
                    printStream.print('\u2657');
                    break;
                case WHITE_KING:
                    printStream.print('\u2654');
                    break;
                case WHITE_KNIGHT:
                    printStream.print('\u2658');
                    break;
                case WHITE_PAWN:
                    printStream.print('\u2659');
                    break;
                case WHITE_QUEEN:
                    printStream.print('\u2655');
                    break;
                case WHITE_ROOK:
                    printStream.print('\u2656');
                    break;
                default:
                    printStream.print(" ");
            }
            if (++row % 8 == 0) {
                printStream.println();
            }
        }
    }
}
