package com.github.bhlangonijr.chesslib;

import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import com.github.bhlangonijr.chesslib.move.MoveList;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * The type Perft test.
 */
public class PerftTest {

    private static final boolean CHECK_BOARD_STATE = false;

    /**
     * Test perft 1.
     *
     * @throws MoveGeneratorException the move generator exception
     */
    @Test
    public void testPerft1() throws MoveGeneratorException {

        long nodes = testPerft("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", 5);
        assertEquals(4865609, nodes);
    }

    /**
     * Test perft 2.
     *
     * @throws MoveGeneratorException the move generator exception
     */
    @Test
    public void testPerft2() throws MoveGeneratorException {

        long nodes = testPerft("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8", 4);
        assertEquals(2103487, nodes);
    }

    /**
     * Test perft 3.
     *
     * @throws MoveGeneratorException the move generator exception
     */
    @Test
    public void testPerft3() throws MoveGeneratorException {

        long nodes = testPerft("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10", 4);
        assertEquals(3894594, nodes);
    }

    /**
     * Test perft 4.
     *
     * @throws MoveGeneratorException the move generator exception
     */
    @Test
    public void testPerft4() throws MoveGeneratorException {

        long nodes = testPerft("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -", 4);
        assertEquals(4085603, nodes);
    }

    /**
     * Test perft 5.
     *
     * @throws MoveGeneratorException the move generator exception
     */
    @Test
    public void testPerft5() throws MoveGeneratorException {

        long nodes = testPerft("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8", 4);
        assertEquals(2103487, nodes);
    }

    /**
     * Test perft 6.
     *
     * @throws MoveGeneratorException the move generator exception
     */
    @Test
    public void testPerft6() throws MoveGeneratorException {

        long nodes = testPerft("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1", 4);
        assertEquals(422333, nodes);
    }

    /**
     * Test perft 7.
     *
     * @throws MoveGeneratorException the move generator exception
     */
    @Test
    public void testPerft7() throws MoveGeneratorException {

        long nodes = testPerft("r3k2r/1b4bq/8/8/8/8/7B/R3K2R w KQkq - 0 1", 4);
        assertEquals(1274206, nodes);
    }

    /**
     * Test perft 8.
     *
     * @throws MoveGeneratorException the move generator exception
     */
    @Test
    public void testPerft8() throws MoveGeneratorException {

        long nodes = testPerft("r3k2r/8/3Q4/8/8/5q2/8/R3K2R b KQkq - 0 1", 4);
        assertEquals(1720476, nodes);
    }

    /**
     * Test perft 9.
     *
     * @throws MoveGeneratorException the move generator exception
     */
    @Test
    public void testPerft9() throws MoveGeneratorException {

        long nodes = testPerft("8/8/1P2K3/8/2n5/1q6/8/5k2 b - - 0 1", 5);
        assertEquals(1004658, nodes);
    }

    /**
     * Test perft 10.
     *
     * @throws MoveGeneratorException the move generator exception
     */
    @Test
    public void testPerft10() throws MoveGeneratorException {

        long nodes = testPerft("4k3/1P6/8/8/8/8/K7/8 w - - 0 1", 6);
        assertEquals(217342, nodes);
    }

    /**
     * Test perft 11.
     *
     * @throws MoveGeneratorException the move generator exception
     */
    @Test
    public void testPerft11() throws MoveGeneratorException {

        long nodes = testPerft("8/P1k5/K7/8/8/8/8/8 w - - 0 1", 6);
        assertEquals(92683, nodes);
    }

    /**
     * Test perft 12.
     *
     * @throws MoveGeneratorException the move generator exception
     */
    @Test
    public void testPerft12() throws MoveGeneratorException {

        long nodes = testPerft("K1k5/8/P7/8/8/8/8/8 w - - 0 1", 6);
        assertEquals(2217, nodes);
    }

    /**
     * Test perft 13.
     *
     * @throws MoveGeneratorException the move generator exception
     */
    @Test
    public void testPerft13() throws MoveGeneratorException {

        long nodes = testPerft("8/k1P5/8/1K6/8/8/8/8 w - - 0 1", 7);
        assertEquals(567584, nodes);
    }

    /**
     * Test perft 14.
     *
     * @throws MoveGeneratorException the move generator exception
     */
    @Test
    public void testPerft14() throws MoveGeneratorException {

        long nodes = testPerft("8/8/2k5/5q2/5n2/8/5K2/8 b - - 0 1", 4);
        assertEquals(23527, nodes);
    }

    /**
     * Test perft long.
     *
     * @param fen   the fen
     * @param depth the depth
     * @return the long
     * @throws MoveGeneratorException the move generator exception
     */
    public long testPerft(String fen, int depth) throws MoveGeneratorException {

        Board board = new Board();
        board.setEnableEvents(false);
        board.loadFromFen(fen);

        return perft(board, depth, 1);
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
        int hash = 0;
        if (CHECK_BOARD_STATE) hash = board.hashCode();
        MoveList moves = MoveGenerator.generateLegalMoves(board);
        for (Move move : moves) {
            try {
                if (!board.doMove(move, false)) {
                    continue;
                }
                partialNodes = perft(board, depth - 1, ply + 1);
                nodes += partialNodes;
                if (ply == 1) {
                    System.out.println(move.toString() + ": " + partialNodes);
                }
                board.undoMove();
                if (CHECK_BOARD_STATE && hash != board.hashCode()) {
                    throw new IllegalArgumentException("Illegal board state after move: " + move);
                }
            } catch (Exception e) {

                System.err.println("depth " + depth + " - ply " + ply);
                e.printStackTrace();
                throw new IllegalArgumentException(e);
            }
        }
        if (ply == 1) {
            System.out.println("Node count: " + nodes);
            System.out.println("Time: " + (System.currentTimeMillis() - time));
        }
        return nodes;
    }

    private void test() {

        Board board = new Board();
        board.addEventListener(BoardEventType.ON_MOVE, new MyBoardListener());


    }

    /**
     * The type My board listener.
     */
    class MyBoardListener implements BoardEventListener {

        public void onEvent(BoardEvent event) {

            if (event.getType() == BoardEventType.ON_MOVE) {
                Move move = (Move) event;
                System.out.println("Move " + move + " was played");
            }
        }
    }

}
