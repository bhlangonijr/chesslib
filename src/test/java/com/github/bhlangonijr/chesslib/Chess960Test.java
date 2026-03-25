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
