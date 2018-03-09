package com.github.bhlangonijr.chesslib;

import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import com.github.bhlangonijr.chesslib.move.MoveList;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class PerftTest {

    @Test
    public void testPerft1() throws MoveGeneratorException, CloneNotSupportedException {

        Board board = new Board();
        board.setEnableEvents(false);
        board.loadFromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

        long nodes = perft(board, 5, 1);
        assertEquals(4865609, nodes);

    }

    @Test
    public void testPerft2() throws MoveGeneratorException, CloneNotSupportedException {

        Board board = new Board();
        board.setEnableEvents(false);
        board.loadFromFEN("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");

        long nodes = perft(board, 4, 1);
        assertEquals(2103487, nodes);

    }

    private long perft(Board board, int depth, int ply) throws MoveGeneratorException, CloneNotSupportedException {

        if (depth == 0) {
            return 1;
        }
        long time = 0;
        if (ply == 1) {
            time = System.currentTimeMillis();
        }
        long nodes = 0;
        long partialNodes;
        MoveList moves = MoveGenerator.getInstance().generateLegalMoves(board);

        for (Move move: moves)  {
            try {
                Board b = board.clone();
                if (!b.doMove(move)) {
                    continue;
                }
                partialNodes = perft(b, depth - 1, ply + 1);
                nodes += partialNodes;
                if (ply == 1) {
                    System.out.println(move.toString() + ": " + partialNodes);
                }
                //board.undoMove();
            } catch (Exception e) {

                System.err.println(move.toString());
                System.err.println(board.getFEN());
                System.err.println(board);

                System.err.println("depth " + depth + " - ply " + ply);
                e.printStackTrace();
                throw new IllegalArgumentException(e);
            }
        }
        if (ply == 1) {
            System.out.println("Node count: " + nodes);
            System.out.println("Time: " + (System.currentTimeMillis() - time ));
        }
        return nodes;


    }

}
