package com.github.bhlangonijr.chesslib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.github.bhlangonijr.chesslib.move.Move;

/**
 * Tests Chess960 castling via doMove() using king-to-rook-square notation (UCI convention).
 * Covers various king/rook placements for both sides, kingside and queenside.
 * <p>
 * In Chess960 UCI notation, castling is encoded as king moves to the rook's square.
 * After castling: king goes to g1/c1 (white) or g8/c8 (black),
 * rook goes to f1/d1 (white) or f8/d8 (black).
 */
public class Chess960DoMoveBugTest {

    // ========================================================================
    // WHITE QUEENSIDE (O-O-O): King → C1, Rook → D1
    // ========================================================================

    @Test
    public void testWhiteOOO_KingOnB_RookOnA() {
        // King b1, rook a1 — the original bug case
        assertCastling("8/8/8/8/8/8/8/RK6 w A - 0 1",
                Square.B1, Square.A1,  // king moves to rook square
                Square.C1, Square.D1); // expected final positions
    }

    @Test
    public void testWhiteOOO_KingOnC_RookOnA() {
        // King c1, rook a1 — king already on destination, rook moves
        assertCastling("8/8/8/8/8/8/8/R1K5 w A - 0 1",
                Square.C1, Square.A1,
                Square.C1, Square.D1);
    }

    @Test
    public void testWhiteOOO_KingOnD_RookOnA() {
        // King d1, rook a1
        assertCastling("8/8/8/8/8/8/8/R2K4 w A - 0 1",
                Square.D1, Square.A1,
                Square.C1, Square.D1);
    }

    @Test
    public void testWhiteOOO_KingOnE_RookOnA() {
        // King e1 (standard), rook a1
        assertCastling("8/8/8/8/8/8/8/R3K3 w A - 0 1",
                Square.E1, Square.A1,
                Square.C1, Square.D1);
    }

    @Test
    public void testWhiteOOO_KingOnF_RookOnA() {
        // King f1, rook a1
        assertCastling("8/8/8/8/8/8/8/R4K2 w A - 0 1",
                Square.F1, Square.A1,
                Square.C1, Square.D1);
    }

    @Test
    public void testWhiteOOO_KingOnD_RookOnB() {
        // King d1, rook b1
        assertCastling("8/8/8/8/8/8/8/1R1K4 w B - 0 1",
                Square.D1, Square.B1,
                Square.C1, Square.D1);
    }

    @Test
    public void testWhiteOOO_KingOnE_RookOnB() {
        // King e1, rook b1
        assertCastling("8/8/8/8/8/8/8/1R2K3 w B - 0 1",
                Square.E1, Square.B1,
                Square.C1, Square.D1);
    }

    // ========================================================================
    // WHITE KINGSIDE (O-O): King → G1, Rook → F1
    // ========================================================================

    @Test
    public void testWhiteOO_KingOnB_RookOnH() {
        // King b1, rook h1
        assertCastling("8/8/8/8/8/8/8/1K5R w H - 0 1",
                Square.B1, Square.H1,
                Square.G1, Square.F1);
    }

    @Test
    public void testWhiteOO_KingOnE_RookOnH() {
        // King e1 (standard), rook h1
        assertCastling("8/8/8/8/8/8/8/4K2R w H - 0 1",
                Square.E1, Square.H1,
                Square.G1, Square.F1);
    }

    @Test
    public void testWhiteOO_KingOnE_RookOnF() {
        // King e1, rook f1 — adjacent, kingside
        assertCastling("8/8/8/8/8/8/8/4KR2 w F - 0 1",
                Square.E1, Square.F1,
                Square.G1, Square.F1);
    }

    @Test
    public void testWhiteOO_KingOnF_RookOnH() {
        // King f1, rook h1
        assertCastling("8/8/8/8/8/8/8/5K1R w H - 0 1",
                Square.F1, Square.H1,
                Square.G1, Square.F1);
    }

    @Test
    public void testWhiteOO_KingOnF_RookOnG() {
        // King f1, rook g1 — adjacent, kingside
        assertCastling("8/8/8/8/8/8/8/5KR1 w G - 0 1",
                Square.F1, Square.G1,
                Square.G1, Square.F1);
    }

    @Test
    public void testWhiteOO_KingOnD_RookOnF() {
        // King d1, rook f1
        assertCastling("8/8/8/8/8/8/8/3K1R2 w F - 0 1",
                Square.D1, Square.F1,
                Square.G1, Square.F1);
    }

    // ========================================================================
    // BLACK QUEENSIDE (O-O-O): King → C8, Rook → D8
    // ========================================================================

    @Test
    public void testBlackOOO_KingOnB_RookOnA() {
        // King b8, rook a8
        assertCastling("rk6/8/8/8/8/8/8/8 b a - 0 1",
                Square.B8, Square.A8,
                Square.C8, Square.D8);
    }

    @Test
    public void testBlackOOO_KingOnD_RookOnA() {
        // King d8, rook a8
        assertCastling("r2k4/8/8/8/8/8/8/8 b a - 0 1",
                Square.D8, Square.A8,
                Square.C8, Square.D8);
    }

    @Test
    public void testBlackOOO_KingOnE_RookOnB() {
        // King e8, rook b8
        assertCastling("1r2k3/8/8/8/8/8/8/8 b b - 0 1",
                Square.E8, Square.B8,
                Square.C8, Square.D8);
    }

    // ========================================================================
    // BLACK KINGSIDE (O-O): King → G8, Rook → F8
    // ========================================================================

    @Test
    public void testBlackOO_KingOnB_RookOnH() {
        // King b8, rook h8
        assertCastling("1k5r/8/8/8/8/8/8/8 b h - 0 1",
                Square.B8, Square.H8,
                Square.G8, Square.F8);
    }

    @Test
    public void testBlackOO_KingOnE_RookOnH() {
        // King e8 (standard), rook h8
        assertCastling("4k2r/8/8/8/8/8/8/8 b h - 0 1",
                Square.E8, Square.H8,
                Square.G8, Square.F8);
    }

    @Test
    public void testBlackOO_KingOnF_RookOnG() {
        // King f8, rook g8 — adjacent
        assertCastling("5kr1/8/8/8/8/8/8/8 b g - 0 1",
                Square.F8, Square.G8,
                Square.G8, Square.F8);
    }

    // ========================================================================
    // Original bug case from game Rohith vs Kobalia
    // ========================================================================

    @Test
    public void testOriginalBugPosition() {
        String fen = "rkb1rbqn/pppp1ppp/2n1p3/2b1p3/2B1P3/1P2N3/PBPP1PPP/RK2R1QN w EAea - 4 5";
        Board board = new Board();
        board.loadFromFen(fen, true);

        assertEquals(Square.B1, board.getKingSquare(Side.WHITE));

        // O-O-O: king b1 → rook a1 (UCI notation)
        Move castlingMove = new Move(Square.B1, Square.A1);
        board.doMove(castlingMove);

        assertEquals("King should be on C1", Square.C1, board.getKingSquare(Side.WHITE));
        // Verify rook ended on D1
        assertEquals("Rook should be on D1 after O-O-O",
                Piece.WHITE_ROOK, board.getPiece(Square.D1));
    }

    // ========================================================================
    // Helper
    // ========================================================================

    private void assertCastling(String fen, Square kingFrom, Square rookSquare,
                                 Square expectedKingDest, Square expectedRookDest) {
        Board board = new Board();
        board.loadFromFen(fen, true);

        assertEquals("King should start on " + kingFrom, kingFrom, board.getKingSquare(
                kingFrom.getRank() == Rank.RANK_1 ? Side.WHITE : Side.BLACK));

        // UCI Chess960 castling: king moves to rook's square
        Move castlingMove = new Move(kingFrom, rookSquare);
        boolean result = board.doMove(castlingMove);

        assertTrue("doMove should succeed for castling " + kingFrom + "→" + rookSquare, result);

        Side side = kingFrom.getRank() == Rank.RANK_1 ? Side.WHITE : Side.BLACK;
        Square actualKing = board.getKingSquare(side);
        assertEquals("King should end on " + expectedKingDest + " (was " + actualKing + ")",
                expectedKingDest, actualKing);

        Piece expectedRook = Piece.make(side, PieceType.ROOK);
        assertEquals("Rook should be on " + expectedRookDest,
                expectedRook, board.getPiece(expectedRookDest));
    }
}
