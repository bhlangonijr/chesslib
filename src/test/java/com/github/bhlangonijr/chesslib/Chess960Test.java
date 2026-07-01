package com.github.bhlangonijr.chesslib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.github.bhlangonijr.chesslib.game.VariationType;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;

public class Chess960Test {

    @Test
    public void testChess960Detection() {
        // Position: bnqbrnkr - King on G1, Rooks on E1 and H1
        Board board = new Board();
        board.loadFromFen("bnqbrnkr/pppppppp/8/8/8/8/PPPPPPPP/BNQBRNKR w KQkq - 0 1");
        assertEquals(VariationType.CHESS960, board.getContext().getVariationType());
    }

    @Test
    public void testChess960ShredderFenDetection() {
        // Shredder-FEN with explicit rook files
        Board board = new Board();
        board.loadFromFen("bnqbrnkr/pppppppp/8/8/8/8/PPPPPPPP/BNQBRNKR w EHeh - 0 1");
        assertEquals(VariationType.CHESS960, board.getContext().getVariationType());
    }

    @Test
    public void testChess960CastlingOO() {
        // Position: bnqbrnkr - King on G1, Rooks on E1 and H1
        Board board = new Board();
        board.loadFromFen("bnqbrnkr/pppppppp/8/8/8/8/PPPPPPPP/BNQBRNKR w KQkq - 0 1");

        // Play 1. e4 e5 2. b3 Ng6 3. g3
        board.doMove(new Move(Square.E2, Square.E4));
        board.doMove(new Move(Square.E7, Square.E5));
        board.doMove(new Move(Square.B2, Square.B3));
        board.doMove(new Move(Square.F8, Square.G6)); // Knight f8->g6
        board.doMove(new Move(Square.G2, Square.G3));

        // Black should be able to castle O-O (king G8, rook H8)
        // O-O = king G8->G8 (stays!), rook H8->F8
        Move castleMove = board.getContext().getoo(Side.BLACK);
        assertNotNull(castleMove);
        assertTrue(board.legalMoves().contains(castleMove));

        // Execute the castle
        assertTrue(board.doMove(castleMove));

        // Verify: king on G8, rook on F8
        assertEquals(Piece.BLACK_KING, board.getPiece(Square.G8));
        assertEquals(Piece.BLACK_ROOK, board.getPiece(Square.F8));
        assertEquals(Piece.NONE, board.getPiece(Square.H8));
    }

    @Test
    public void testChess960CastlingUndo() {
        Board board = new Board();
        board.loadFromFen("bnqbrnkr/pppppppp/8/8/8/8/PPPPPPPP/BNQBRNKR w KQkq - 0 1");

        board.doMove(new Move(Square.E2, Square.E4));
        board.doMove(new Move(Square.E7, Square.E5));
        board.doMove(new Move(Square.B2, Square.B3));
        board.doMove(new Move(Square.F8, Square.G6));
        board.doMove(new Move(Square.G2, Square.G3));

        String fenBefore = board.getFen();

        Move castleMove = board.getContext().getoo(Side.BLACK);
        board.doMove(castleMove);

        // Undo the castle
        board.undoMove();

        // Board should be back to the state before castling
        assertEquals(fenBefore, board.getFen());
        assertEquals(Piece.BLACK_KING, board.getPiece(Square.G8));
        assertEquals(Piece.BLACK_ROOK, board.getPiece(Square.H8));
    }

    @Test
    public void testChess960SanParsing() {
        Board board = new Board();
        board.loadFromFen("bnqbrnkr/pppppppp/8/8/8/8/PPPPPPPP/BNQBRNKR w KQkq - 0 1");

        // Play moves via SAN
        MoveList moves = new MoveList(board.getFen());
        moves.loadFromSan("e4 e5 b3 Ng6 g3 O-O");

        assertEquals(6, moves.size());
    }

    @Test
    public void testChess960LoadFromTextUci() {
        // loadFromText uses UCI notation — Chess960 castling is king→rook (g8h8)
        Board board = new Board();
        board.loadFromFen("bnqbrnkr/pppppppp/8/8/8/8/PPPPPPPP/BNQBRNKR w KQkq - 0 1");

        MoveList moves = new MoveList(board.getFen());
        // e4 e5 b3 Ng6 g3 O-O (O-O = king g8 to rook h8 in UCI Chess960)
        moves.loadFromText("e2e4 e7e5 b2b3 f8g6 g2g3 g8h8");

        assertEquals(6, moves.size());

        // Replay and verify the castling happened correctly
        for (Move m : moves) {
            assertTrue("Move should be legal: " + m, board.doMove(m, true));
        }
        assertEquals(Piece.BLACK_KING, board.getPiece(Square.G8));
        assertEquals(Piece.BLACK_ROOK, board.getPiece(Square.F8));
    }

    @Test
    public void testChess960FullGame() {
        Board board = new Board();
        board.loadFromFen("bnqbrnkr/pppppppp/8/8/8/8/PPPPPPPP/BNQBRNKR w KQkq - 0 1");

        MoveList moves = new MoveList(board.getFen());
        moves.loadFromSan("e4 e5 b3 Ng6 g3 O-O");

        for (Move m : moves) {
            assertTrue("Move should be legal: " + m, board.doMove(m, true));
        }
    }

    @Test
    public void testChess960FenRoundTrip() {
        // Load a Chess960 position and verify FEN round-trip
        Board board = new Board();
        board.loadFromFen("bnqbrnkr/pppppppp/8/8/8/8/PPPPPPPP/BNQBRNKR w EHeh - 0 1");

        String fen = board.getFen();
        // Should output Shredder-FEN
        assertTrue("FEN should contain Shredder-FEN castling: " + fen,
                fen.contains("HE") || fen.contains("he"));

        // Reload and verify
        Board board2 = new Board();
        board2.loadFromFen(fen);
        assertEquals(VariationType.CHESS960, board2.getContext().getVariationType());
    }

    @Test
    public void testChess960WhiteCastleOO() {
        // White king on F1, rook on H1 - standard-ish O-O
        Board board = new Board();
        board.loadFromFen("rnbqkbnr/pppppppp/8/8/8/5N2/PPPPPPPP/RNBQK2R w KQkq - 0 1");
        // This is actually standard chess (king on E1), so it should NOT be Chess960
        assertEquals(VariationType.NORMAL, board.getContext().getVariationType());
    }

    @Test
    public void testChess960CastleRightsLostOnRookMove() {
        Board board = new Board();
        board.loadFromFen("bnqbrnkr/pppppppp/8/8/8/8/PPPPPPPP/BNQBRNKR w KQkq - 0 1");

        // Move the h-side rook (H1)
        board.doMove(new Move(Square.H1, Square.G1)); // This is actually the king square...
        // Let's try a different approach - move a pawn first to open the rook
        Board board2 = new Board();
        board2.loadFromFen("bnqbrnkr/pppppppp/8/8/8/8/PPPPPPPP/BNQBRNKR w KQkq - 0 1");
        board2.doMove(new Move(Square.H2, Square.H4));
        board2.doMove(new Move(Square.A7, Square.A6));
        board2.doMove(new Move(Square.H1, Square.H3)); // Move the h-rook

        // White should lose king-side castling right
        CastleRight cr = board2.getCastleRight(Side.WHITE);
        assertTrue(cr == CastleRight.QUEEN_SIDE || cr == CastleRight.NONE);
    }

    @Test
    public void testChess960UciConversion() {
        // Position: bnqbrnkr - King on G, Rooks on E and H
        Board board = new Board();
        board.loadFromFen("bnqbrnkr/pppppppp/8/8/8/8/PPPPPPPP/BNQBRNKR w KQkq - 0 1");

        // O-O for white: king G1, rook H1 → UCI should be "g1h1" (king to rook)
        Move whiteOO = board.getContext().getoo(Side.WHITE);
        assertEquals("g1h1", board.toUci(whiteOO));

        // O-O-O for white: king G1, rook E1 → UCI should be "g1e1"
        Move whiteOOO = board.getContext().getooo(Side.WHITE);
        assertEquals("g1e1", board.toUci(whiteOOO));

        // fromUci: "g1h1" should be recognized as O-O
        Move parsed = board.fromUci("g1h1");
        assertEquals(whiteOO, parsed);

        // fromUci: "g1e1" should be recognized as O-O-O
        Move parsedOOO = board.fromUci("g1e1");
        assertEquals(whiteOOO, parsedOOO);

        // Normal move: "e2e4" should work as usual
        Move e4 = board.fromUci("e2e4");
        assertEquals(Square.E2, e4.getFrom());
        assertEquals(Square.E4, e4.getTo());
        assertEquals("e2e4", board.toUci(e4));
    }

    @Test
    public void testStandardChessUciUnaffected() {
        Board board = new Board();
        // Standard chess: O-O is e1g1, not e1h1
        Move whiteOO = board.getContext().getoo(Side.WHITE);
        assertEquals("e1g1", board.toUci(whiteOO));
    }

    @Test
    public void testStandardChessUnaffected() {
        // Verify standard chess still works
        Board board = new Board();
        board.loadFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        assertEquals(VariationType.NORMAL, board.getContext().getVariationType());

        // Play standard opening with castling
        MoveList moves = new MoveList();
        moves.loadFromSan("e4 e5 Nf3 Nc6 Bb5 a6 Ba4 Nf6 O-O");
        for (Move m : moves) {
            assertTrue(board.doMove(m, true));
        }
        assertEquals(Piece.WHITE_KING, board.getPiece(Square.G1));
        assertEquals(Piece.WHITE_ROOK, board.getPiece(Square.F1));
    }

    // ===== FEN FORMAT TESTS =====
    // These tests verify the FEN output format for Chess960 positions,
    // specifically the Shredder-FEN castling notation (file letters vs KQkq).

    @Test
    public void testShredderFenOutputWhenLoadedWithShredderFen() {
        // Position: bqnbnrkr - King on G, Rooks on F and H
        // Loaded with Shredder-FEN → getFen() should output Shredder-FEN
        Board board = new Board();
        board.loadFromFen("bqnbnrkr/pppppppp/8/8/8/8/PPPPPPPP/BQNBNRKR w HFhf - 0 1");

        String fen = board.getFen();
        String castling = fen.split(" ")[2];
        assertEquals("Shredder-FEN should be preserved", "HFhf", castling);
    }

    @Test
    public void testShredderFenOutputWhenLoadedWithKQkqAndChess960Flag() {
        // Same position loaded with KQkq + chess960=true
        // getFen() should output Shredder-FEN (file letters) because the rook files are detected
        Board board = new Board();
        board.loadFromFen("bqnbnrkr/pppppppp/8/8/8/8/PPPPPPPP/BQNBNRKR w KQkq - 0 1", true);

        String fen = board.getFen();
        String castling = fen.split(" ")[2];
        assertEquals("Chess960 flag should produce Shredder-FEN", "HFhf", castling);
    }

    @Test
    public void testKQkqOutputWhenLoadedWithKQkqWithoutFlag() {
        // Same position loaded with KQkq WITHOUT chess960=true
        // King is NOT on e-file → auto-detected as Chess960 → Shredder-FEN
        Board board = new Board();
        board.loadFromFen("bqnbnrkr/pppppppp/8/8/8/8/PPPPPPPP/BQNBNRKR w KQkq - 0 1");

        String fen = board.getFen();
        String castling = fen.split(" ")[2];
        // King on G-file (not E) → auto-detected as Chess960
        assertEquals("Non-standard king position auto-detects Chess960", "HFhf", castling);
    }

    @Test
    public void testShredderFenPreservedAfterMoves() {
        // Load Chess960 position, play some moves, verify FEN stays in Shredder format
        Board board = new Board();
        board.loadFromFen("bqnbnrkr/pppppppp/8/8/8/8/PPPPPPPP/BQNBNRKR w HFhf - 0 1");

        board.doMove(new Move(Square.E2, Square.E4)); // 1. e4
        String fen1 = board.getFen();
        String castling1 = fen1.split(" ")[2];
        assertEquals("Shredder-FEN preserved after pawn move", "HFhf", castling1);

        board.doMove(new Move(Square.E7, Square.E5)); // 1... e5
        String fen2 = board.getFen();
        String castling2 = fen2.split(" ")[2];
        assertEquals("Shredder-FEN preserved after black pawn move", "HFhf", castling2);
    }

    @Test
    public void testShredderFenAfterCastlingRightsLost() {
        // When one side loses castling rights, the remaining rights should still be Shredder
        Board board = new Board();
        board.loadFromFen("bqnbnrkr/pppppppp/8/8/8/8/PPPPPPPP/BQNBNRKR w HFhf - 0 1");

        // Move the H-rook (king-side) → lose king-side castling
        board.doMove(new Move(Square.H2, Square.H4));
        board.doMove(new Move(Square.A7, Square.A6));
        board.doMove(new Move(Square.H1, Square.H3)); // Move H-rook

        String fen = board.getFen();
        String castling = fen.split(" ")[2];
        // White should only have queen-side (F-file rook), black still has both
        assertTrue("Castling should contain F for white queen-side: " + castling,
                castling.contains("F"));
        // White should have lost king-side (H) castling right
        // Check that no uppercase H remains in the castling string
        boolean hasWhiteKingSide = false;
        for (char c : castling.toCharArray()) {
            if (c == 'H') { hasWhiteKingSide = true; break; }
        }
        assertTrue("Castling should NOT contain H for white king-side: " + castling,
                !hasWhiteKingSide);
    }

    @Test
    public void testFenHashConsistencyBetweenShredderAndKQkqLoad() {
        // CRITICAL: Verify that loading the same position with Shredder-FEN vs KQkq+flag
        // produces the SAME getFen() output (both should be Shredder-FEN)
        Board board1 = new Board();
        board1.loadFromFen("bqnbnrkr/pppppppp/8/8/8/8/PPPPPPPP/BQNBNRKR w HFhf - 0 1");

        Board board2 = new Board();
        board2.loadFromFen("bqnbnrkr/pppppppp/8/8/8/8/PPPPPPPP/BQNBNRKR w KQkq - 0 1", true);

        assertEquals("Both loading methods should produce identical FEN",
                board1.getFen(), board2.getFen());
    }

    @Test
    public void testShredderFenWithStandardRookPositions() {
        // Position 518 (standard starting position) — rooks on A and H files
        // Even in Chess960 mode, rooks on a/h should produce standard-looking Shredder-FEN
        Board board = new Board();
        board.loadFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w HAha - 0 1");

        String fen = board.getFen();
        String castling = fen.split(" ")[2];
        // H=king-side, A=queen-side → "HAha"
        assertEquals("Standard rook positions in Shredder-FEN", "HAha", castling);
    }

    @Test
    public void testFenRoundTripWithRealChess960Game() {
        // Real game FEN from Freestyle Chess Grand Slam: bqnbnrkr position
        String startFen = "bqnbnrkr/pppppppp/8/8/8/8/PPPPPPPP/BQNBNRKR w HFhf - 0 1";
        Board board = new Board();
        board.loadFromFen(startFen);

        // Play 1. c4 b6 2. b4 e6
        board.doMove(new Move(Square.C2, Square.C4));
        board.doMove(new Move(Square.B7, Square.B6));
        board.doMove(new Move(Square.B2, Square.B4));
        board.doMove(new Move(Square.E7, Square.E6));

        // Get FEN after moves
        String fenAfterMoves = board.getFen();

        // Reload from that FEN and verify round-trip
        Board board2 = new Board();
        board2.loadFromFen(fenAfterMoves);
        assertEquals("FEN round-trip should be identical", fenAfterMoves, board2.getFen());
        assertEquals("Should still be Chess960", VariationType.CHESS960, board2.getContext().getVariationType());
    }

    // ===== EN PASSANT FEN TESTS =====
    // These tests verify the en passant square behavior in FEN output.
    // Standard FEN: always outputs ep square after double pawn push
    // X-FEN: only outputs ep square when a legal en passant capture exists
    // chesslib Board.getFen(): outputs ep square ALWAYS (standard FEN behavior)
    // chesslib Board.getFen(true, true): outputs ep square only if capturable (X-FEN behavior)

    @Test
    public void testEnPassantAlwaysOutputByDefault() {
        // After 1. e4, the ep square should be e3 even though no black pawn can capture
        Board board = new Board();
        board.doMove(new Move(Square.E2, Square.E4));
        String fen = board.getFen();
        String epField = fen.split(" ")[3];
        assertEquals("Default getFen() always outputs ep square after double push", "e3", epField);
    }

    @Test
    public void testEnPassantOnlyIfCapturableOption() {
        // After 1. e4, with onlyOutputEnPassantIfCapturable=true, ep should be "-"
        Board board = new Board();
        board.doMove(new Move(Square.E2, Square.E4));
        String fen = board.getFen(true, true);
        String epField = fen.split(" ")[3];
        assertEquals("getFen(true, true) omits ep when no capture possible", "-", epField);
    }

    @Test
    public void testEnPassantOutputWhenCaptureExists() {
        // After 1. e4 d5 2. e5 f5, the ep square f6 should be output in both modes
        Board board = new Board();
        board.doMove(new Move(Square.E2, Square.E4));
        board.doMove(new Move(Square.D7, Square.D5));
        board.doMove(new Move(Square.E4, Square.E5));
        board.doMove(new Move(Square.F7, Square.F5)); // f5 next to e5 pawn

        String fenDefault = board.getFen();
        String epDefault = fenDefault.split(" ")[3];
        assertEquals("Default: ep square present when capture exists", "f6", epDefault);

        String fenStrict = board.getFen(true, true);
        String epStrict = fenStrict.split(" ")[3];
        assertEquals("Strict: ep square present when capture exists", "f6", epStrict);
    }

    @Test
    public void testEnPassantChess960DefaultBehavior() {
        // In Chess960, the default getFen() also always outputs ep square
        Board board = new Board();
        board.loadFromFen("bqnbnrkr/pppppppp/8/8/8/8/PPPPPPPP/BQNBNRKR w HFhf - 0 1");
        board.doMove(new Move(Square.E2, Square.E4));
        String fen = board.getFen();
        String epField = fen.split(" ")[3];
        assertEquals("Chess960 default: ep square always output", "e3", epField);
    }

    @Test
    public void testEnPassantChess960StrictBehavior() {
        // In Chess960, getFen(true, true) omits ep when no capture possible
        Board board = new Board();
        board.loadFromFen("bqnbnrkr/pppppppp/8/8/8/8/PPPPPPPP/BQNBNRKR w HFhf - 0 1");
        board.doMove(new Move(Square.E2, Square.E4));
        String fen = board.getFen(true, true);
        String epField = fen.split(" ")[3];
        assertEquals("Chess960 strict: ep square omitted when no capture", "-", epField);
    }

    @Test
    public void testExplicitChess960FlagWithKingOnE() {
        // Chess960 position 518 (identical to standard) but with rooks on B1 and F1
        // King on E1, rooks NOT on A1/H1 — without the flag, KQkq would be treated as standard
        String fen = "nrbkqbnr/pppppppp/8/8/8/8/PPPPPPPP/NRBKQBNR w KQkq - 0 1";

        // Without flag: king on E1 → detected as standard (wrong for this position)
        Board boardStd = new Board();
        boardStd.loadFromFen(fen);
        // King is not on E1 here actually... let me use a real position 518 variant
        // Position where king IS on e1 but rooks are on b1 and f1
        String fen960 = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RN1QKBNR w KQkq - 0 1";
        // Actually let's use a cleaner example: king on E, rooks on B and H
        String fenKingOnE = "qnrbknbr/pppppppp/8/8/8/8/PPPPPPPP/QNRBKNBR w KQkq - 0 1";

        // With explicit flag: forced Chess960
        Board board960 = new Board();
        board960.loadFromFen(fenKingOnE, true);
        assertEquals(VariationType.CHESS960, board960.getContext().getVariationType());

        // Without flag: king on E but non-standard piece arrangement — still detected as NORMAL
        // because king IS on e-file
        Board boardNormal = new Board();
        boardNormal.loadFromFen(fenKingOnE);
        assertEquals(VariationType.NORMAL, boardNormal.getContext().getVariationType());
    }
}
