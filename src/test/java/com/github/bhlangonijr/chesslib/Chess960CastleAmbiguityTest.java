package com.github.bhlangonijr.chesslib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.List;

import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;

/**
 * Chess960 positions where a castle move and a plain king move were confused.
 * <p>
 * A Chess960 castle move is stored as the king moving onto its own rook (UCI Chess960 convention).
 * Stored as the king moving to its final square instead, it equals a plain king step whenever
 * that square is next to the king. All positions below were found by comparing every legal move
 * against python-chess in randomly played Chess960 games.
 */
public class Chess960CastleAmbiguityTest {

    /**
     * King b1, rook a1: Kc1 and O-O-O both bring the king to c1. Kc1 must stay a plain king move.
     */
    @Test
    public void testKingStepToCastleSquareIsNotCastle() {
        String fen = "rk4r1/pppppppp/8/8/8/8/PPPPPPPP/RK4R1 w GAga - 0 1";
        Board board = new Board();
        board.loadFromFen(fen);

        List<Move> legalMoves = board.legalMoves();
        assertEquals("Kc1 once", 1, count(legalMoves, new Move(Square.B1, Square.C1)));
        assertEquals("O-O-O once", 1, count(legalMoves, new Move(Square.B1, Square.A1)));

        assertTrue(board.doMove(new Move(Square.B1, Square.C1)));
        assertEquals(Piece.WHITE_KING, board.getPiece(Square.C1));
        assertEquals(Piece.WHITE_ROOK, board.getPiece(Square.A1));
        assertEquals(Piece.NONE, board.getPiece(Square.D1));

        board.loadFromFen(fen);
        assertTrue(board.doMove(new Move(Square.B1, Square.A1)));
        assertEquals(Piece.WHITE_KING, board.getPiece(Square.C1));
        assertEquals(Piece.WHITE_ROOK, board.getPiece(Square.D1));
        assertEquals(Piece.NONE, board.getPiece(Square.A1));
    }

    /**
     * King f8, rook h8, g8 attacked by the knight on e7: neither Kg8 nor O-O is legal. Kg8 used to
     * be taken for O-O and skipped the attack check on its destination.
     */
    @Test
    public void testKingStepIntoAttackIsNotLegal() {
        Board board = new Board();
        board.loadFromFen("bnrbnk1r/4N1pp/1p1p1p2/2Q1p3/p4PP1/P2N4/qPPPP2P/B1RBK2R b hc - 0 9");

        for (Move move : board.legalMoves()) {
            assertFalse("illegal " + move, move.getFrom() == Square.F8
                    && (move.getTo() == Square.G8 || move.getTo() == Square.H8));
        }
        assertFalse(board.doMove(new Move(Square.F8, Square.G8), true));
        assertFalse(board.doMove(new Move(Square.F8, Square.H8), true));
    }

    /**
     * Black may castle queenside only, and the king has no legal move at all. Kg8 used to be taken
     * for O-O with the rook on h8 and was played.
     */
    @Test
    public void testNoCastleWithoutRightForThatSide() {
        Board board = new Board();
        board.loadFromFen("n1rbbk1r/2p1p1p1/2n1Bp2/1p1q4/pP2p2p/N4PPP/P1P1KR2/N1R1B1Q1 b c - 5 15");

        for (Move move : board.legalMoves()) {
            assertFalse("illegal " + move, move.getFrom() == Square.F8);
        }
        assertFalse(board.doMove(new Move(Square.F8, Square.G8), true));
        assertFalse(board.doMove(new Move(Square.F8, Square.H8), true));
    }

    /**
     * Undoing Kc1 lost the bishop on d1, because the backup took Kc1 for O-O-O although white may
     * castle kingside only.
     */
    @Test
    public void testUndoRestoresEveryMove() {
        String fen = "1kbb2r1/3pq2n/r3p1n1/1pp2ppp/PPPP3P/2B2N2/4PPP1/RK1B2QR w H - 3 19";
        Board board = new Board();
        board.loadFromFen(fen);
        String before = board.getFen();

        for (Move move : board.legalMoves()) {
            assertTrue(board.doMove(move));
            board.undoMove();
            assertEquals("after undoing " + move, before, board.getFen());
        }
    }

    /**
     * The black rook on b8 shields c8 from the white queen on a8. After O-O-O it stands on d8 and
     * the king on c8 would be in check, so O-O-O is illegal.
     */
    @Test
    public void testCastleRookMayNotShieldKingDestination() {
        Board board = new Board();
        board.loadFromFen("Qr1k4/nb1p1rq1/4pb1p/2NP1pp1/p3BP1P/PNp1K1P1/1P2P1R1/1RB5 b b - 6 22");

        Move castle = new Move(Square.D8, Square.B8);
        assertFalse(board.legalMoves().contains(castle));
        assertFalse(board.doMove(castle, true));
    }

    /**
     * Once the h1 rook has left, Kh1 is a plain king move and must not be written as O-O.
     */
    @Test
    public void testSanOfKingMoveToFormerRookSquare() throws Exception {
        MoveList moves = new MoveList("1r4kr/8/8/8/8/8/8/1R4KR w HBhb - 0 1");
        moves.add(new Move(Square.H1, Square.H4));
        moves.add(new Move(Square.B8, Square.B7));
        moves.add(new Move(Square.G1, Square.H1));

        assertEquals("Kh1", moves.toSanArray()[2]);
    }

    /**
     * UCI and internal notation agree: castling is the king moving onto its own rook.
     */
    @Test
    public void testUciCastleIsKingToRook() {
        Board board = new Board();
        board.loadFromFen("rk4r1/pppppppp/8/8/8/8/PPPPPPPP/RK4R1 w GAga - 0 1");

        Move castle = board.fromUci("b1a1");
        assertEquals(new Move(Square.B1, Square.A1), castle);
        assertEquals("b1a1", board.toUci(castle));
        assertEquals(new Move(Square.B1, Square.C1), board.fromUci("b1c1"));
        assertEquals("b1c1", board.toUci(new Move(Square.B1, Square.C1)));
    }

    private static int count(List<Move> moves, Move move) {
        int n = 0;
        for (Move m : moves) {
            if (m.equals(move)) {
                n++;
            }
        }
        return n;
    }
}
