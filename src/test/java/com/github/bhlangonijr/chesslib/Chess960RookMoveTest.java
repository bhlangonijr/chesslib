package com.github.bhlangonijr.chesslib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.github.bhlangonijr.chesslib.game.VariationType;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;

/**
 * Minimal test to reproduce the Chess960 Ra1 bug from TWIC 1639 (Aronian vs Kumala).
 * <p>
 * The game starts from FEN: rkqrbbnn/pppppppp/8/8/8/8/PPPPPPPP/RKQRBBNN w KQkq - 0 1
 * At move 43, White plays Ra1 (rook from a4 to a1) which is a simple rook move,
 * but chesslib rejects it as illegal in Chess960 mode.
 */
public class Chess960RookMoveTest {

    private static final String START_FEN = "rkqrbbnn/pppppppp/8/8/8/8/PPPPPPPP/RKQRBBNN w KQkq - 0 1";

    // SAN moves for the first 42 moves (84 half-moves), before 43. Ra1
    private static final String MOVES_SAN =
            "1. d4 d5 2. f3 Bb5 3. a4 Ba6 4. Nf2 Ng6 5. Nd3 Bxd3 6. exd3 e6 7. f4 Bd6 8. g3 " +
            "h5 9. Nf3 f6 10. c4 c6 11. Bh3 Qd7 12. Qe3 Re8 13. Nd2 Nh6 14. Qf3 h4 15. Nb3 " +
            "Qf7 16. a5 a6 17. Ra4 Ka7 18. Bb4 Bxb4 19. Rxb4 hxg3 20. Nc5 Rab8 21. hxg3 Nf8 " +
            "22. Rc1 f5 23. Qd1 Nd7 24. Na4 Qf6 25. Qd2 Nf7 26. Bg2 Nd6 27. Bf3 g6 28. Bd1 " +
            "Rh8 29. Bb3 Rh3 30. Nb6 Nxb6 31. axb6+ Ka8 32. c5 Nf7 33. Rg1 Rbh8 34. Rg2 g5 " +
            "35. Ka2 g4 36. Ra4 Kb8 37. Re2 Nd8 38. Rg2 R8h7 39. Bc2 Qh8 40. Kb3 Rh2 41. Rxh2 " +
            "Rxh2 42. Qe3 Kc8";

    /**
     * Test that demonstrates the Chess960 fix: when the board is loaded with the initial
     * Chess960 FEN (which has castling rights), the context defines castling as B8→C8.
     * After castling rights are lost, the king can still move from B8 to C8 as a normal move.
     * Previously this was rejected because isCastleMove matched the coordinates without
     * checking castling rights.
     */
    @Test
    public void testRa1IsLegalInChess960Position() throws Exception {
        // Load the initial Chess960 position with castling rights
        Board board = new Board();
        board.loadFromFen(START_FEN, true);

        assertTrue("Should be Chess960", board.getContext().getVariationType() == VariationType.CHESS960);

        // The context defines blackooo = Move(B8, C8) for queen-side castling
        Move blackOOO = board.getContext().getooo(Side.BLACK);
        System.out.println("Black OOO (queen-side castle): " + blackOOO);
        assertEquals("Black OOO should be B8->C8", new Move(Square.B8, Square.C8), blackOOO);

        // Play all 84 half-moves via MoveList — this should now succeed
        // (previously failed at move 42...Kc8 because it was confused with castling)
        MoveList moveList = new MoveList(START_FEN);
        moveList.loadFromSan(MOVES_SAN);
        assertEquals("Should have 84 half-moves", 84, moveList.size());

        // Get the FEN after 42 moves and verify Ra1 is legal
        String fenBeforeRa1 = moveList.getFen();
        System.out.println("FEN before 43. Ra1: " + fenBeforeRa1);

        Board boardAfter42 = new Board();
        boardAfter42.loadFromFen(fenBeforeRa1, true);

        // Verify the rook is on a4 and a1 is empty
        assertEquals("White rook should be on A4", Piece.WHITE_ROOK, boardAfter42.getPiece(Square.A4));
        assertEquals("A1 should be empty", Piece.NONE, boardAfter42.getPiece(Square.A1));

        // Ra1 should be legal
        Move ra1 = new Move(Square.A4, Square.A1);
        assertTrue("Ra1 (a4->a1) should be legal", boardAfter42.isMoveLegal(ra1, true));

        // Play Ra1 and verify it succeeds
        assertTrue("doMove(Ra1) should succeed", boardAfter42.doMove(ra1, true));
        assertEquals("Rook should now be on A1", Piece.WHITE_ROOK, boardAfter42.getPiece(Square.A1));
    }
}
