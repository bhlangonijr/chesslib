package com.github.bhlangonijr.chesslib.move;

import com.github.bhlangonijr.chesslib.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * The type Move generator test.
 */
public class MoveGeneratorTest {


    /**
     * Test b bto square.
     */
    @Test
    public void testBBtoSquare() {
        assertEquals(Piece.make(Side.BLACK, PieceType.PAWN).value(), "BLACK_PAWN");
        long pieces = (1L << 10) | (1L << 63) | (1L << 45);

        List<Square> sqs = Bitboard.bbToSquareList(pieces);
        assertEquals(sqs.size(), Arrays.asList(Square.C2, Square.F6, Square.H8).size());

    }

    /**
     * Test all move generation.
     *
     * @throws MoveGeneratorException the move generator exception
     */
    @Test
    public void testAllMoveGeneration() throws MoveGeneratorException {
        Board board = new Board();

        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", board.getFen());
        MoveList moves = MoveGenerator.generateLegalMoves(board);
        assertEquals(moves.size(), 20);

        String fen = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 0";
        board.loadFromFen(fen);
        assertEquals("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 0", board.getFen());
        moves = MoveGenerator.generateLegalMoves(board);
        assertEquals(moves.size(), 48);

        fen = "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 0";
        board.loadFromFen(fen);
        assertEquals("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 0", board.getFen());
        moves = MoveGenerator.generateLegalMoves(board);
        assertEquals(moves.size(), 14);

    }

    @Test
    public void testAllCapturesGeneration() {

        Board board = new Board();
        board.loadFromFen("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 0");

        MoveList moves = MoveGenerator.generatePseudoLegalCaptures(board);


        assertTrue(moves.contains(new Move("g2h3", Side.WHITE)));
        assertTrue(moves.contains(new Move("d5e6", Side.WHITE)));
        assertTrue(moves.contains(new Move("e5g6", Side.WHITE)));
        assertTrue(moves.contains(new Move("e5d7", Side.WHITE)));
        assertTrue(moves.contains(new Move("e5f7", Side.WHITE)));
        assertTrue(moves.contains(new Move("e2a6", Side.WHITE)));
        assertTrue(moves.contains(new Move("f3h3", Side.WHITE)));
        assertTrue(moves.contains(new Move("f3f6", Side.WHITE)));
        assertEquals(8, moves.size());

        board.loadFromFen("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 0");

        moves = MoveGenerator.generatePseudoLegalCaptures(board);

        assertTrue(moves.contains(new Move("b4f4", Side.WHITE)));
        assertEquals(1, moves.size());

        board.loadFromFen(Constants.startStandardFENPosition);

        moves = MoveGenerator.generatePseudoLegalCaptures(board);

        assertEquals(0, moves.size());
    }

}
