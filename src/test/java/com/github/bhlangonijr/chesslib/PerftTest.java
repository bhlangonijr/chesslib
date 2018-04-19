package com.github.bhlangonijr.chesslib;

import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import com.github.bhlangonijr.chesslib.move.MoveList;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class PerftTest {

    @Test
    public void testPerft1() throws MoveGeneratorException {

        Board board = new Board();
        board.setEnableEvents(false);
        board.loadFromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

        long nodes = perft(board, 5, 1);
        assertEquals(4865609, nodes);
    }

    @Test
    public void testPerft2() throws MoveGeneratorException {

        Board board = new Board();
        board.setEnableEvents(false);
        board.loadFromFEN("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");

        long nodes = perft(board, 4, 1);
        assertEquals(2103487, nodes);
    }

    @Test
    public void testPerft3() throws MoveGeneratorException {

        Board board = new Board();
        board.setEnableEvents(false);
        board.loadFromFEN("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10");

        long nodes = perft(board, 4, 1);
        assertEquals(3894594, nodes);
    }

    @Test
    public void testPerft4() throws MoveGeneratorException {

        Board board = new Board();
        board.setEnableEvents(false);
        board.loadFromFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");

        long nodes = perft(board, 4, 1);
        assertEquals(4085603, nodes);
    }

    @Test
    public void testPerft5() throws MoveGeneratorException {

        Board board = new Board();
        board.setEnableEvents(false);
        board.loadFromFEN("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");

        long nodes = perft(board, 4, 1);


        System.out.println(board);
        System.out.println(board.getFEN());
        assertEquals(2103487, nodes);
    }

    @Test
    public void testPerft6() throws MoveGeneratorException {

        Board board = new Board();
        board.setEnableEvents(false);
        board.loadFromFEN("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");

        long nodes = perft(board, 4, 1);
        assertEquals(422333, nodes);
    }

    private long perft(Board board, int depth, int ply) throws MoveGeneratorException {

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
                if (!board.doMove(move, true)) {
                    continue;
                }
                partialNodes = perft(board, depth - 1, ply + 1);
                nodes += partialNodes;
                if (ply == 1) {
                    System.out.println(move.toString() + ": " + partialNodes);
                }
                board.undoMove();
            } catch (Exception e) {

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
