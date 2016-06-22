package com.github.bhlangonijr.chesslib;

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PgnHolderTest {

    @Test
    public void testPGNLoad1() throws Exception {

        PgnHolder pgn = new PgnHolder("src/test/resources/cct131.pgn");
        pgn.loadPgn();
        Game game = pgn.getGame().get(0);
        game.loadMoveText();

        assertEquals(3, pgn.getGame().size());
        assertEquals("Rookie", game.getWhitePlayer().getName());
        assertEquals("JabbaChess", game.getBlackPlayer().getName());
        assertEquals("2011.01.29", game.getDate());
        assertEquals(2, game.getRound().getNumber());
        assertEquals("1-0", game.getResult().getDescription());
        assertEquals("0", game.getPlyCount());
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
    public void testPGNLoad2() throws Exception {

        PgnHolder pgn = new PgnHolder("src/test/resources/rav_alternative.pgn");
        pgn.loadPgn();
        Game game = pgn.getGame().get(0);
        game.loadMoveText();

        assertEquals(1, pgn.getGame().size());
        assertEquals("Ponomariov, Ruslan", game.getWhitePlayer().getName());
        assertEquals("Ivanchuk, Vassily", game.getBlackPlayer().getName());
        assertEquals("2002.02.23", game.getDate());
        assertEquals(1, game.getRound().getNumber());
        assertEquals("1-0", game.getResult().getDescription());
        assertEquals("0", game.getPlyCount());
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

    @Test
    public void testPGNLoad3() throws Exception {

        PgnHolder pgn = new PgnHolder("src/test/resources/linares_2002.pgn");
        pgn.loadPgn();
        Game game = pgn.getGame().get(1);
        game.loadMoveText();

        assertEquals(42, pgn.getGame().size());
        assertEquals("Shirov, Alexei", game.getWhitePlayer().getName());
        assertEquals("Anand, Viswanathan", game.getBlackPlayer().getName());
        assertEquals("2002.02.23", game.getDate());
        assertEquals(1, game.getRound().getNumber());
        assertEquals("1/2-1/2", game.getResult().getDescription());
        assertEquals("0", game.getPlyCount());
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

}
