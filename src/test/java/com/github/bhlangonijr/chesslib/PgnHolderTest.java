package com.github.bhlangonijr.chesslib;

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.github.bhlangonijr.chesslib.pgn.PgnException;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;
import com.github.bhlangonijr.chesslib.util.LargeFile;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * The type Pgn holder test.
 */
public class PgnHolderTest {

    /**
     * Test pgn load 1.
     *
     * @throws Exception the exception
     */
    @Test
    public void testPGNLoad1() throws Exception {

        PgnHolder pgn = new PgnHolder("src/test/resources/cct131.pgn");
        pgn.loadPgn();
        Game game = pgn.getGames().get(0);
        game.loadMoveText();

        assertEquals(3, pgn.getGames().size());
        assertEquals("Rookie", game.getWhitePlayer().getName());
        assertEquals("JabbaChess", game.getBlackPlayer().getName());
        assertEquals("2011.01.29", game.getDate());
        assertEquals(2, game.getRound().getNumber());
        assertEquals("1-0", game.getResult().getDescription());
        assertEquals("67", game.getPlyCount());
        assertEquals("Albert Silver", game.getAnnotator());
        assertEquals(2285, game.getWhitePlayer().getElo());
        assertEquals(1680, game.getBlackPlayer().getElo());

        assertEquals("C00", game.getEco());
        assertEquals(67, game.getHalfMoves().size());
        assertEquals("e2e4 e7e6 d2d4 a7a6 g1f3 d7d5 e4d5 e6d5 f1d3 b8c6 e1g1 g8f6 f1e1 f8e7 c2c3 e8g8 b1d2 f8e8 f3e5 " +
                "c6e5 d4e5 f6d7 d2b3 g7g6 b3d4 c7c5 d4f3 b7b5 c1h6 c8b7 h2h4 e7h4 a2a4 b5b4 c3b4 c5b4 d1c1 h4e7 c1f4 " +
                "d7c5 a1d1 b7c6 e5e6 f7f6 f3h4 c6a4 h4g6 c5e6 e1e6 e7d6 f4g4 d6h2 g1h2 d8c7 h2g1 c7g7 e6e7 e8e7 g6e7 " +
                "g8f7 g4g7 f7e8 e7d5 a8a7 d3b5 a6b5 d5f6", game.getHalfMoves().toString());

    }

    /**
     * Test pgn load 2.
     *
     * @throws Exception the exception
     */
    @Test
    public void testPGNLoad2() throws Exception {

        PgnHolder pgn = new PgnHolder("src/test/resources/rav_alternative.pgn");
        pgn.loadPgn();
        Game game = pgn.getGames().get(0);
        game.loadMoveText();

        assertEquals(1, pgn.getGames().size());
        assertEquals("Ponomariov, Ruslan", game.getWhitePlayer().getName());
        assertEquals("Ivanchuk, Vassily", game.getBlackPlayer().getName());
        assertEquals("2002.02.23", game.getDate());
        assertEquals(1, game.getRound().getNumber());
        assertEquals("1-0", game.getResult().getDescription());
        assertEquals("89", game.getPlyCount());
        assertEquals("Hathaway, Mark", game.getAnnotator());
        assertEquals(2727, game.getWhitePlayer().getElo());
        assertEquals(2717, game.getBlackPlayer().getElo());

        assertEquals("C18", game.getEco());
        assertEquals(89, game.getHalfMoves().size());
        assertEquals("e2e4 e7e6 d2d4 d7d5 b1c3 f8b4 e4e5 c7c5 a2a3 b4c3 b2c3 g8e7 d1g4 e8g8 f1d3 f7f5 e5f6 f8f6 c1g5" +
                " f6f7 g4h5 g7g6 h5d1 b8c6 g1f3 d8f8 e1g1 c5c4 d3e2 h7h6 g5c1 c8d7 f3e1 g6g5 g2g3 e7f5 e1g2 f8g7 f2f4" +
                " f5d6 d1e1 b7b5 f4g5 f7f1 e2f1 h6g5 g2e3 a8f8 f1g2 a7a5 c1d2 g7g6 e3g4 f8f5 e1e3 g8g7 a1b1 g7h7 a3a4" +
                " b5a4 d2c1 f5f7 c1a3 g6c2 b1c1 c2f5 g2h3 d6e4 g4e5 f5f2 e3f2 f7f2 e5d7 f2a2 a3c5 e4d2 h3g2 a4a3 d7f8" +
                " h7h6 c1e1 e6e5 d4e5 g5g4 e5e6 d2f3 g2f3 g4f3 g1f1", game.getHalfMoves().toString());

    }

    /**
     * Test pgn load 3.
     *
     * @throws Exception the exception
     */
    @Test
    public void testPGNLoad3() throws Exception {

        PgnHolder pgn = new PgnHolder("src/test/resources/linares_2002.pgn");
        pgn.loadPgn();
        Game game = pgn.getGames().get(1);
        game.loadMoveText();

        assertEquals(42, pgn.getGames().size());
        assertEquals("Shirov, Alexei", game.getWhitePlayer().getName());
        assertEquals("Anand, Viswanathan", game.getBlackPlayer().getName());
        assertEquals("2002.02.23", game.getDate());
        assertEquals(1, game.getRound().getNumber());
        assertEquals("1/2-1/2", game.getResult().getDescription());
        assertEquals("104", game.getPlyCount());
        assertEquals("Hathaway, Mark", game.getAnnotator());
        assertEquals(2715, game.getWhitePlayer().getElo());
        assertEquals(2757, game.getBlackPlayer().getElo());

        assertEquals("B49", game.getEco());
        assertEquals(104, game.getHalfMoves().size());
        assertEquals("e2e4 c7c5 g1f3 e7e6 d2d4 c5d4 f3d4 b8c6 b1c3 d8c7 f1e2 a7a6 e1g1 g8f6 c1e3 f8b4 c3a4 b4e7 d4c6" +
                " b7c6 a4b6 a8b8 b6c8 c7c8 e3d4 e8g8 d1d3 c8c7 b2b3 a6a5 a1d1 d7d5 e4d5 f6d5 e2f3 f8d8 c2c4 d5f6 d3c3" +
                " e7d6 g2g3 c6c5 d4f6 g7f6 c3f6 d6e7 f6h6 c7e5 d1e1 e5f6 h6h5 e7f8 e1e4 d8d4 e4d4 f6d4 f1e1 d4f6 e1e4" +
                " f8g7 e4f4 f6e7 f4h4 h7h6 h4g4 f7f5 g4g6 g8h7 g3g4 b8f8 g4f5 f8f5 f3e4 f5h5 g6e6 h7h8 e6e7 h5e5 e7e5" +
                " g7e5 g1g2 h8g7 e4b1 g7f6 h2h4 e5f4 g2f3 f6e5 f3g4 f4c1 f2f3 c1d2 g4h5 e5f6 b1c2 d2c1 c2e4 c1d2 e4d5" +
                " d2c1 a2a4 c1e3 d5g8 e3d2", game.getHalfMoves().toString());

    }

    /**
     * Test pgn load 4.
     *
     * @throws Exception the exception
     */
    @Test
    public void testPGNLoad4() throws Exception {

        PgnHolder pgn = new PgnHolder("src/test/resources/redqueen.pgn");
        pgn.loadPgn();
        Game game = pgn.getGames().get(1);
        game.loadMoveText();

        assertEquals(270, pgn.getGames().size());
        assertEquals("Amoeba 1.2 64-bit", game.getWhitePlayer().getName());
        assertEquals("RedQueen 1.1.98 64-bit", game.getBlackPlayer().getName());
        assertEquals("2016.06.08", game.getDate());
        assertEquals(1, game.getRound().getNumber());
        assertEquals("1-0", game.getResult().getDescription());
        assertEquals("97", game.getPlyCount());
        assertEquals(null, game.getAnnotator());
        assertEquals(0, game.getWhitePlayer().getElo());
        assertEquals(0, game.getBlackPlayer().getElo());

        assertEquals("B90", game.getEco());
        assertEquals(97, game.getHalfMoves().size());
        assertEquals("e2e4 c7c5 g1f3 d7d6 d2d4 c5d4 f3d4 g8f6 b1c3 a7a6 f2f3 d8b6 d4b3 e7e6 g2g4 b8c6 d1e2 b6c7 c1e3 " +
                        "b7b5 e1c1 f6d7 c1b1 d7b6 e2f2 a8b8 h2h4 f8e7 f1b5 a6b5 c3b5 c7b7 e3b6 e8g8 b6c7 b7b5 h4h5 c8a6 h5h6 " +
                        "g7g6 c7d6 b5e2 f2g3 b8d8 b3c1 e7d6 d1d6 e2b5 h1d1 b5g5 d6d8 f8d8 d1d8 g5d8 f3f4 c6d4 f4f5 e6f5 e4f5 " +
                        "a6b7 g3f4 d4f3 c1d3 b7e4 d3f2 e4d5 b2b3 g8h8 f2d3 g6g5 f4e3 d5e4 b1b2 e4d5 e3c5 f7f6 a2a4 f3h2 a4a5 " +
                        "h2g4 c5d4 g4h6 d3b4 g5g4 b4d5 g4g3 a5a6 g3g2 a6a7 h6g8 d4c5 g2g1q c5g1 d8d5 g1b6 d5e5 b2a3",
                game.getHalfMoves().toString());

    }


    @Test
    public void testPromotionWithMissingEqualSign() throws Exception {

        PgnHolder pgn = new PgnHolder("src/test/resources/promoting.pgn");
        pgn.loadPgn();
        Game game = pgn.getGames().get(0);
        game.loadMoveText();
        MoveList moves = game.getHalfMoves();
        Board board = new Board();
        for (Move move : moves) {
            board.doMove(move);
        }
        assertEquals(moves.toString(), "g1f3 d7d5 e2e3 g8f6 c2c4 e7e6 d2d4 c7c5 a2a3 b8c6 d4c5 f8c5 b2b4 c5e7 " +
                "c1b2 e8g8 b1d2 a7a5 b4b5 c6b8 f1e2 b8d7 e1g1 b7b6 a1c1 c8b7 c4d5 f6d5 d2c4 d7c5 f3d4 e7f6 e2f3 e6e5 " +
                "d4c6 b7c6 b5c6 e5e4 b2f6 d5f6 d1d8 a8d8 c4b6 c5d3 c1c3 e4f3 c6c7 f6e4 c7d8q f8d8 c3c8 d8c8 b6c8 g7g5 " +
                "g2f3 e4d2 f1d1 d2f3 g1g2 f3e5 c8e7 g8g7 e7c6 d3f2 g2f2 e5c6 d1d5 f7f6 d5c5 c6e5 f2e2 h7h5 c5a5 g5g4 " +
                "a5e5 f6e5 a3a4");
    }

    @Test
    public void testCupPgn() throws Exception {

        PgnHolder pgn = new PgnHolder("src/test/resources/cup.pgn");
        pgn.loadPgn();
        for (Game game : pgn.getGames()) {
            game.loadMoveText();
            MoveList moves = game.getHalfMoves();
            Board board = new Board();
            for (Move move : moves) {
                board.doMove(move);
            }
        }
    }

    @Test
    public void testOO() throws Exception {

        PgnHolder pgn = new PgnHolder("src/test/resources/oo.pgn");
        pgn.loadPgn();
        for (Game game : pgn.getGames()) {
            game.loadMoveText();
            MoveList moves = game.getHalfMoves();
            Board board = new Board();
            for (Move move : moves) {
                board.doMove(move);
            }
        }
    }

    @Test
    public void testEP() throws Exception {

        PgnHolder pgn = new PgnHolder("src/test/resources/ep.pgn");
        pgn.loadPgn();
        for (Game game : pgn.getGames()) {
            game.loadMoveText();
            MoveList moves = game.getHalfMoves();
            Board board = new Board();
            for (Move move : moves) {
                board.doMove(move);
            }
        }
    }

    @Test
    public void testZ0() throws Exception {

        PgnHolder pgn = new PgnHolder("src/test/resources/z0.pgn");
        pgn.loadPgn();
        for (Game game : pgn.getGames()) {
            game.loadMoveText();
            MoveList moves = game.getHalfMoves();
            Board board = new Board();
            for (Move move : moves) {
                board.doMove(move);
            }
        }
    }

    @Test(expected = PgnException.class)
    public void testErr() throws Exception {

        PgnHolder pgn = new PgnHolder("src/test/resources/err.pgn");
        pgn.loadPgn();
        for (Game game : pgn.getGames()) {
            game.loadMoveText();
            MoveList moves = game.getHalfMoves();
            Board board = new Board();
            for (Move move : moves) {
                board.doMove(move);
            }
        }
    }

    @Test
    public void testAnsi() throws Exception {

        PgnHolder pgn = new PgnHolder("src/test/resources/Morphy_ANSI.pgn");
        pgn.loadPgn();
        for (Game game : pgn.getGames()) {
            game.loadMoveText();
            MoveList moves = game.getHalfMoves();
            Board board = new Board();
            for (Move move : moves) {
                board.doMove(move);
            }
        }
    }

    @Test
    public void testUtf8() throws Exception {

        PgnHolder pgn = new PgnHolder("src/test/resources/Morphy_UTF8.pgn");
        pgn.loadPgn();
        for (Game game : pgn.getGames()) {
            game.loadMoveText();
            MoveList moves = game.getHalfMoves();
            Board board = new Board();
            for (Move move : moves) {
                board.doMove(move);
            }
        }
    }

    @Test
    public void testLongMoves() throws Exception {

        long init = System.currentTimeMillis();
        PgnHolder pgn = new PgnHolder("src/test/resources/longest.pgn");
        pgn.loadPgn();
        for (Game game : pgn.getGames()) {
            game.loadMoveText();
            MoveList moves = game.getHalfMoves();
            Board board = new Board();
            for (Move move : moves) {
                board.doMove(move);
            }
        }
        long timeSpent = System.currentTimeMillis() - init;
        assertTrue(timeSpent < 5000);
    }

    @Test
    public void testRepetition() throws Exception {

        final PgnHolder pgn = new PgnHolder("src/test/resources/test.pgn");
        pgn.loadPgn();
        final Game game = pgn.getGames().get(0);
        game.loadMoveText();

        final Board board = new Board();
        for (final Move move : game.getHalfMoves()) {
            board.doMove(move);
        }
        assertFalse(board.isRepetition());
    }

    @Test
    public void testRepetition2() throws Exception {

        final PgnHolder pgn = new PgnHolder("src/test/resources/alekseenko_grachev_2017.pgn");
        pgn.loadPgn();
        final Game game = pgn.getGames().get(0);
        game.loadMoveText();

        final Board board = new Board();
        for (final Move move : game.getHalfMoves()) {
            board.doMove(move);
        }
        assertFalse(board.isRepetition());
    }

    @Test
    public void testRepetition3() throws Exception {

        final PgnHolder pgn = new PgnHolder("src/test/resources/nikolic_arsovic_1989.pgn");
        pgn.loadPgn();
        final Game game = pgn.getGames().get(0);
        game.loadMoveText();

        final Board board = new Board();
        for (final Move move : game.getHalfMoves()) {
            board.doMove(move);
        }
        assertFalse(board.isRepetition());
    }

    @Test
    public void testRepetition4() throws Exception {

        final PgnHolder pgn = new PgnHolder("src/test/resources/no_repetition_1.pgn");
        pgn.loadPgn();
        final Game game = pgn.getGames().get(0);
        game.loadMoveText();

        final Board board = new Board();
        for (final Move move : game.getHalfMoves()) {
            board.doMove(move);
        }
        assertFalse(board.isRepetition());
    }

    @Test
    public void testRepetition5() throws Exception {

        final PgnHolder pgn = new PgnHolder("src/test/resources/no_repetition_2.pgn");
        pgn.loadPgn();
        final Game game = pgn.getGames().get(0);
        game.loadMoveText();

        final Board board = new Board();
        for (final Move move : game.getHalfMoves()) {
            board.doMove(move);
        }
        assertFalse(board.isRepetition());
    }

    @Test
    public void testPGNLoadInputStream() throws Exception {

        PgnHolder pgn = new PgnHolder(null);
        pgn.loadPgn(new LargeFile(this.getClass().getResourceAsStream("/cct131.pgn")));
        Game game = pgn.getGames().get(0);
        game.loadMoveText();

        assertEquals(3, pgn.getGames().size());
        assertEquals("Rookie", game.getWhitePlayer().getName());
        assertEquals("JabbaChess", game.getBlackPlayer().getName());
        assertEquals("2011.01.29", game.getDate());
        assertEquals(2, game.getRound().getNumber());
        assertEquals("1-0", game.getResult().getDescription());
        assertEquals("67", game.getPlyCount());
        assertEquals("Albert Silver", game.getAnnotator());
        assertEquals(2285, game.getWhitePlayer().getElo());
        assertEquals(1680, game.getBlackPlayer().getElo());

        assertEquals("C00", game.getEco());
        assertEquals(67, game.getHalfMoves().size());
        assertEquals("e2e4 e7e6 d2d4 a7a6 g1f3 d7d5 e4d5 e6d5 f1d3 b8c6 e1g1 g8f6 f1e1 f8e7 c2c3 e8g8 b1d2 f8e8 f3e5 " +
                "c6e5 d4e5 f6d7 d2b3 g7g6 b3d4 c7c5 d4f3 b7b5 c1h6 c8b7 h2h4 e7h4 a2a4 b5b4 c3b4 c5b4 d1c1 h4e7 c1f4 " +
                "d7c5 a1d1 b7c6 e5e6 f7f6 f3h4 c6a4 h4g6 c5e6 e1e6 e7d6 f4g4 d6h2 g1h2 d8c7 h2g1 c7g7 e6e7 e8e7 g6e7 " +
                "g8f7 g4g7 f7e8 e7d5 a8a7 d3b5 a6b5 d5f6", game.getHalfMoves().toString());
    }

    @Test
    public void testPGNLoadFromString() throws Exception {

        String lines = "[Event \"CCT 13\"]\n" +
                "[Site \"FICS, San Jose, California US\"]\n" +
                "[Date \"2011.01.29\"]\n" +
                "[Round \"2\"]\n" +
                "[White \"Rookie\"]\n" +
                "[Black \"JabbaChess\"]\n" +
                "[Result \"1-0\"]\n" +
                "[ECO \"C00\"]\n" +
                "[WhiteElo \"2285\"]\n" +
                "[BlackElo \"1680\"]\n" +
                "[Annotator \"Albert Silver\"]\n" +
                "[PlyCount \"67\"]\n" +
                "[EventDate \"2011.??.??\"]\n" +
                "[TimeControl \"3000+3\"]\n" +
                "\n" +
                "1. e4 e6 2. d4 a6 3. Nf3 d5 4. exd5 exd5 5. Bd3 Nc6 6. O-O Nf6 7. Re1+ Be7 8.\n" +
                "c3 O-O 9. Nbd2 Re8 10. Ne5 Nxe5 11. dxe5 Nd7 12. Nb3 g6 13. Nd4 c5 14. Nf3 b5\n" +
                "15. Bh6 Bb7 16. h4 Bxh4 17. a4 b4 18. cxb4 cxb4 19. Qc1 Be7 20. Qf4 Nc5 21.\n" +
                "Rad1 Bc6 {#} 22. e6 $1 {A nice combination that takes apart Black's position\n" +
                "with exemplary precision. The variations are no less interesting than the game\n" +
                "continuation, as is typical.} f6 ({The engine obviously goes for the most\n" +
                "resistant defense, however the question is what would happen if Black were to\n" +
                "play a more fallible and human continuation such as} 22... Nxe6 $2 {The answer\n" +
                "is} 23. Rxe6 $1 fxe6 24. Ne5 $1 {The double attack between mate and the bishop\n" +
                "on c6 is decisive.} Rf8 25. Qg4 $1 {and JabbaChess would be forced to leave\n" +
                "the bishop since} Be8 {trying to protect g6, fails to} 26. Bxg6 hxg6 27. Nxg6\n" +
                "Kh7 28. Nxf8+ Bxf8 29. Bxf8 Bg6 30. Bxb4) 23. Nh4 Bxa4 $2 {This is certainly\n" +
                "one of the more suicidal ways to go down.} ({but even the better} 23... Bf8 {\n" +
                "would not hold.} 24. Nxg6 hxg6 25. Qg4 g5 (25... Bxh6 26. Qxg6+ Bg7 27. Qf7+\n" +
                "Kh8 28. Re3 f5 29. Rh3+) 26. Qh5 Qe7 27. Bg6 Nxe6 28. Bxf8 Rxf8 29. Rxe6 Qxe6\n" +
                "30. Qh7#) (23... g5 24. Bxh7+ Kh8 25. Qf5 Rg8 26. Bxg8 Qxg8 27. Ng6+ Kh7 28.\n" +
                "Nxe7+) 24. Nxg6 Nxe6 25. Rxe6 Bd6 26. Qg4 {As the Borg famously said,\n" +
                "\"Resistance is futile\". (Star Trek Next Gen for those who have no idea what I\n" +
                "am talking about).} Bh2+ 27. Kxh2 Qc7+ 28. Kg1 Qg7 {#} 29. Re7 {Typical\n" +
                "computer weirdness. Any normal person would take the queen, but then again,\n" +
                "any normal player would have resigned instead of playing Qg7...} Rxe7 ({Just\n" +
                "in case you wondered how White would finish off Black if the bishop was\n" +
                "captured with} 29... Qxh6 30. Qe6#) 30. Nxe7+ Kf7 31. Qxg7+ Ke8 32. Nxd5 Ra7\n" +
                "33. Bb5+ axb5 34. Nxf6# 1-0\n\n";


        PgnHolder pgn = new PgnHolder(null);
        pgn.loadPgn(lines);
        Game game = pgn.getGames().get(0);
        game.loadMoveText();

        assertEquals(1, pgn.getGames().size());
        assertEquals("Rookie", game.getWhitePlayer().getName());
        assertEquals("JabbaChess", game.getBlackPlayer().getName());
        assertEquals("2011.01.29", game.getDate());
        assertEquals(2, game.getRound().getNumber());
        assertEquals("1-0", game.getResult().getDescription());
        assertEquals("67", game.getPlyCount());
        assertEquals("Albert Silver", game.getAnnotator());
        assertEquals(2285, game.getWhitePlayer().getElo());
        assertEquals(1680, game.getBlackPlayer().getElo());

        assertEquals("C00", game.getEco());
        assertEquals(67, game.getHalfMoves().size());
        assertEquals("e2e4 e7e6 d2d4 a7a6 g1f3 d7d5 e4d5 e6d5 f1d3 b8c6 e1g1 g8f6 f1e1 f8e7 c2c3 e8g8 b1d2 f8e8 f3e5 " +
                "c6e5 d4e5 f6d7 d2b3 g7g6 b3d4 c7c5 d4f3 b7b5 c1h6 c8b7 h2h4 e7h4 a2a4 b5b4 c3b4 c5b4 d1c1 h4e7 c1f4 " +
                "d7c5 a1d1 b7c6 e5e6 f7f6 f3h4 c6a4 h4g6 c5e6 e1e6 e7d6 f4g4 d6h2 g1h2 d8c7 h2g1 c7g7 e6e7 e8e7 g6e7 " +
                "g8f7 g4g7 f7e8 e7d5 a8a7 d3b5 a6b5 d5f6", game.getHalfMoves().toString());
    }

    @Test
    public void testBoardHashKeyConsistency() throws Exception {

        final PgnHolder pgn = new PgnHolder("src/test/resources/Stockfish_DD_64-bit_4CPU.pgn");
        pgn.loadPgn();

        int numberOfInconsistencies = 0;

        for (Game game : pgn.getGames()) {
            game.loadMoveText();
            StringBuilder s = new StringBuilder();
            Map<Long, Integer> map = new HashMap<>();
            Map<String, Integer> map2 = new HashMap<>();
            Map<String, Long> map3 = new HashMap<>();
            final Board board = new Board();
            int i = 0;
            for (final Move move : game.getHalfMoves()) {
                board.doMove(move);
                assertEquals(board.getIncrementalHashKey(), board.getZobristKey());
                s.append(i++);
                s.append(" -> ");
                s.append(board.getFen());
                s.append(" -> ");
                s.append(move);
                s.append(" -> ");
                s.append(board.getIncrementalHashKey());
                s.append("\n");
                map.compute(board.getIncrementalHashKey(), (a, b) -> (b == null) ? 1 : b + 1);
                String key = board.getPositionId();
                map2.compute(key, (a, b) -> (b == null) ? 1 : b + 1);
                map3.put(key, board.getIncrementalHashKey());
            }

            for (Map.Entry<String, Long> entry : map3.entrySet()) {
                if (map.get(entry.getValue()).intValue() != map2.get(entry.getKey()).intValue()) {
                    System.out.println("----------------");
                    System.out.println(entry.getKey() + ":" + entry.getValue() + " [" + map2.get(entry.getKey()) +
                            " - " + map.get(entry.getValue()) + "]");
                    System.out.println(s.toString());
                    numberOfInconsistencies++;
                }
            }
        }
        assertEquals(0, numberOfInconsistencies);
    }
}
