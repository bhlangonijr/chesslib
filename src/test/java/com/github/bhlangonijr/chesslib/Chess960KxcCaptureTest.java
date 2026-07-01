package com.github.bhlangonijr.chesslib;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

import com.github.bhlangonijr.chesslib.move.MoveList;

/**
 * Isolates the Chess960 Kxc1 problem from TWIC 1639 (Schnepp vs Niemann).
 * FEN: rkqrbbnn/pppppppp/8/8/8/8/PPPPPPPP/RKQRBBNN w KQkq - 0 1
 * 
 * The game plays: 1.e4 e5 2.Ng3 Ng6 3.Nf3 c5 4.c3 Nf6 5.d4 cxd4 6.cxd4 Qxc1+ 7.Kxc1 exd4 8.Nxd4 d5
 * Then 9.Nc2 fails.
 *
 * This test uses the stripped FEN (no castling rights) to simulate what the parser does.
 */
public class Chess960KxcCaptureTest {

    private static final String START_FEN = "rkqrbbnn/pppppppp/8/8/8/8/PPPPPPPP/RKQRBBNN w KQkq - 0 1";
    private static final String START_FEN_STRIPPED = "rkqrbbnn/pppppppp/8/8/8/8/PPPPPPPP/RKQRBBNN w - - 0 1";

    /**
     * Test: play moves 1-8 on the stripped FEN, then verify Nc2 is playable at move 9.
     */
    @Test
    public void testNc2AfterKxc1OnStrippedFen() throws Exception {
        // Play moves 1-8 one by one, getting FEN after each
        String[] moves = {"e4", "e5", "Ng3", "Ng6", "Nf3", "c5", "c3", "Nf6", "d4", "cxd4", "cxd4", "Qxc1+", "Kxc1", "exd4", "Nxd4", "d5"};
        
        String currentFen = START_FEN_STRIPPED;
        for (int i = 0; i < moves.length; i++) {
            MoveList ml = new MoveList(currentFen);
            try {
                ml.loadFromSan(moves[i]);
            } catch (Exception e) {
                fail("Move " + moves[i] + " (index " + i + ") failed on FEN: " + currentFen + " — " + e.getMessage());
            }
            currentFen = ml.getFen();
            System.out.println("After " + moves[i] + ": " + currentFen);
        }

        // Now try Nc2
        System.out.println("\nFEN before Nc2: " + currentFen);
        MoveList ml = new MoveList(currentFen);
        try {
            ml.loadFromSan("Nc2");
            System.out.println("Nc2 succeeded! FEN after: " + ml.getFen());
        } catch (Exception e) {
            fail("Nc2 should be legal on FEN: " + currentFen + " — " + e.getMessage());
        }
    }

    /**
     * Test: play moves 1-8 on the FULL FEN (with castling rights), then verify Nc2.
     * This shows whether the castling rights cause the issue.
     */
    @Test
    public void testNc2AfterKxc1OnFullFen() throws Exception {
        String[] moves = {"e4", "e5", "Ng3", "Ng6", "Nf3", "c5", "c3", "Nf6", "d4", "cxd4", "cxd4", "Qxc1+", "Kxc1", "exd4", "Nxd4", "d5"};
        
        String currentFen = START_FEN;
        for (int i = 0; i < moves.length; i++) {
            MoveList ml = new MoveList(currentFen);
            try {
                ml.loadFromSan(moves[i]);
            } catch (Exception e) {
                fail("Move " + moves[i] + " (index " + i + ") failed on FEN: " + currentFen + " — " + e.getMessage());
            }
            currentFen = ml.getFen();
            System.out.println("After " + moves[i] + ": " + currentFen);
        }

        System.out.println("\nFEN before Nc2: " + currentFen);
        MoveList ml = new MoveList(currentFen);
        try {
            ml.loadFromSan("Nc2");
            System.out.println("Nc2 succeeded! FEN after: " + ml.getFen());
        } catch (Exception e) {
            fail("Nc2 should be legal on FEN: " + currentFen + " — " + e.getMessage());
        }
    }

    /**
     * Test: play all moves at once via loadFromSan on stripped FEN.
     * This test verifies that the PGN from TWIC 1639 (Schnepp vs Niemann) has an illegal move
     * at move 16 (Nc2) — the knight is already on c2 and no other knight can reach c2.
     * This is a data error in TWIC, not a parser bug.
     */
    @Test
    public void testAllMovesAtOnceStripped() throws Exception {
        String allMoves = "1. e4 e5 2. Ng3 Ng6 3. Nf3 c5 4. c3 Nf6 5. d4 cxd4 6. cxd4 Qxc1+ 7. Kxc1 exd4 8. Nxd4 d5 9. Nc2 Kc7 10. exd5 Nxd5 11. b3 Ne5 12. Kb2 g6 13. Rac1 Bg7 14. Kb1 Nc6 15. Ne4 Nf4 16. Nc2 Nd3";
        
        MoveList ml = new MoveList(START_FEN_STRIPPED);
        try {
            ml.loadFromSan(allMoves);
            fail("Should fail at move 16 Nc2 (illegal — c2 already occupied by own knight)");
        } catch (Exception e) {
            // Expected: Nc2 is illegal because c2 is already occupied by a white knight
            assertTrue("Error should mention Nc2", e.getMessage().contains("Nc2"));
        }
    }

    /**
     * Test: Grieve vs Jeitz (TWIC 1639, partie 888).
     * FEN: rnqnbkrb/pppppppp/8/8/8/8/PPPPPPPP/RNQNBKRB w KQkq - 0 1
     * The game starts with 1. O-O and ends with 21. Nxe7+.
     * Verifies if Nxe7+ is legal or if the PGN is incorrect.
     */
    @Test
    public void testGrieveJeitzNxe7() throws Exception {
        String fen = "rnqnbkrb/pppppppp/8/8/8/8/PPPPPPPP/RNQNBKRB w KQkq - 0 1";
        String allMoves = "1. O-O d5 2. g3 Bc6 3. d4 Nd7 4. c4 Nf6 5. Ne3 Qd7 6. Nc3 dxc4 7. d5 Bb5 8. Nxb5 Qxb5 9. a4 Qd7 10. Qxc4 O-O 11. Rc1 Rc8 12. Bc3 c5 13. dxc6 Nxc6 14. Rfd1 Qc7 15. b4 Qb8 16. b5 Nd8 17. Nf5 Re8 18. Qb4 Kf8 19. Bxf6 gxf6 20. Qh4 Kg8 21. Nxe7+";

        MoveList ml = new MoveList(fen);
        try {
            ml.loadFromSan(allMoves);
            System.out.println("Grieve vs Jeitz: all moves legal! FEN: " + ml.getFen());
        } catch (Exception e) {
            // Find which move fails
            String[] moves = allMoves.replaceAll("\\d+\\.\\s*", "").trim().split("\\s+");
            String currentFen = fen;
            for (int i = 0; i < moves.length; i++) {
                if (moves[i].isEmpty() || moves[i].matches("\\d+\\..*")) continue;
                MoveList tmp = new MoveList(currentFen);
                try {
                    tmp.loadFromSan(moves[i]);
                    currentFen = tmp.getFen();
                } catch (Exception e2) {
                    System.out.println("FAILED at move '" + moves[i] + "' on FEN: " + currentFen);
                    System.out.println("Error: " + e2.getMessage());
                    fail("Move " + moves[i] + " failed: " + e2.getMessage());
                    break;
                }
            }
        }
    }
}
