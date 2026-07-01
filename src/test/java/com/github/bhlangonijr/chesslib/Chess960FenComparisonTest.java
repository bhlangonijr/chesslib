package com.github.bhlangonijr.chesslib;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import com.github.bhlangonijr.chesslib.move.Move;

/**
 * Tests documenting the exact FEN castling notation output of chesslib for Chess960.
 * These tests serve as a reference for cross-library compatibility with chess.js.
 * 
 * KEY DIFFERENCE:
 * - chesslib: ALWAYS uses file letters for Chess960 (e.g., "HFhf", "EHeh", "HAha")
 * - chess.js: Uses K/Q/k/q for outermost rooks (a/h files), file letters for inner rooks
 *   e.g., "HFhf" → chess.js outputs "KFkf" (H is outermost → K)
 *   e.g., "EHeh" → chess.js outputs "KEke" (H is outermost → K)
 *   e.g., "HAha" → chess.js outputs "KQkq" (both outermost → standard)
 * 
 * CONSEQUENCE: For FEN hash consistency, the frontend must use the FEN from the
 * backend flat tree directly (enrichEvalsFromFlatTree), not recalculate with chess.js.
 */
public class Chess960FenComparisonTest {

    @Test
    public void testChesslibOutputForBqnbnrkr() {
        // bqnbnrkr: King on G, Rooks on F(queen-side) and H(king-side)
        Board board = new Board();
        board.loadFromFen("bqnbnrkr/pppppppp/8/8/8/8/PPPPPPPP/BQNBNRKR w HFhf - 0 1");
        
        String castling = board.getFen().split(" ")[2];
        // chesslib: always file letters → "HFhf"
        // chess.js: H is outermost → "KFkf"
        assertEquals("chesslib always uses file letters", "HFhf", castling);
    }

    @Test
    public void testChesslibOutputForBnqbrnkr() {
        // bnqbrnkr: King on G, Rooks on E(queen-side) and H(king-side)
        Board board = new Board();
        board.loadFromFen("bnqbrnkr/pppppppp/8/8/8/8/PPPPPPPP/BNQBRNKR w EHeh - 0 1");
        
        String castling = board.getFen().split(" ")[2];
        // chesslib: "HEhe" (king-side first, then queen-side — based on code order)
        // Wait, let's check: the code iterates KING_SIDE first, then QUEEN_SIDE
        // King-side rook = H → "H", Queen-side rook = E → "E" → "HE" for white
        // King-side rook = h → "h", Queen-side rook = e → "e" → "he" for black
        assertEquals("chesslib outputs king-side first, then queen-side", "HEhe", castling);
    }

    @Test
    public void testChesslibOutputForStandardPosition518() {
        // Position 518 (standard starting position) loaded as Chess960
        Board board = new Board();
        board.loadFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w HAha - 0 1");
        
        String castling = board.getFen().split(" ")[2];
        // chesslib: H=king-side, A=queen-side → "HAha"
        // chess.js: H is outermost, A is outermost → "KQkq"
        assertEquals("chesslib uses file letters even for standard rook positions", "HAha", castling);
    }

    @Test
    public void testChesslibOutputAfterMoves() {
        Board board = new Board();
        board.loadFromFen("bqnbnrkr/pppppppp/8/8/8/8/PPPPPPPP/BQNBNRKR w HFhf - 0 1");
        
        board.doMove(new Move(Square.E2, Square.E4)); // 1. e4
        String castling1 = board.getFen().split(" ")[2];
        assertEquals("Castling preserved after e4", "HFhf", castling1);
        
        board.doMove(new Move(Square.E7, Square.E5)); // 1... e5
        String castling2 = board.getFen().split(" ")[2];
        assertEquals("Castling preserved after e5", "HFhf", castling2);
    }

    @Test
    public void testChesslibKQkqWithChess960FlagProducesShredder() {
        // Loading with KQkq + chess960=true should auto-detect rook positions
        // and output Shredder-FEN
        Board board = new Board();
        board.loadFromFen("bqnbnrkr/pppppppp/8/8/8/8/PPPPPPPP/BQNBNRKR w KQkq - 0 1", true);
        
        String castling = board.getFen().split(" ")[2];
        assertEquals("KQkq + chess960 flag → Shredder-FEN", "HFhf", castling);
    }

    @Test
    public void testFenNormalizationMustPreserveShredderNotation() {
        // CRITICAL: The backend FenUtils.getNormalizedFen() keeps castling as-is.
        // The frontend fenUtils.js getNormalizedFen() should do the same for Chess960 FEN.
        // If the frontend converts Shredder→KQkq, the hash will NOT match the backend hash.
        //
        // This test verifies that chesslib's FEN output is consistent and can be
        // used directly for hash computation without any conversion.
        Board board = new Board();
        board.loadFromFen("bqnbnrkr/pppppppp/8/8/4P3/8/PPPP1PPP/BQNBNRKR b HFhf - 0 1");
        
        String fen = board.getFen();
        String castling = fen.split(" ")[2];
        assertEquals("Shredder notation preserved after reload", "HFhf", castling);
    }
}
