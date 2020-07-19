package com.github.bhlangonijr.chesslib;

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.pgn.PgnIterator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * The type Pgn iterator test.
 */
public class PgnIteratorTest {

    @Test
    public void testPGNIteration() throws Exception {

        PgnIterator games = new PgnIterator("src/test/resources/cct131.pgn");

        int count = 0;
        for (Game game: games) {
            count++;
            switch (count) {
                case 1: {
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
                    break;
                }
                case 2: {
                    assertEquals("Chirone", game.getWhitePlayer().getName());
                    assertEquals("crafty", game.getBlackPlayer().getName());
                    assertEquals("2011.01.29", game.getDate());
                    assertEquals(2, game.getRound().getNumber());
                    assertEquals("1-0", game.getResult().getDescription());
                    assertEquals("143", game.getPlyCount());
                    assertEquals("Albert Silver", game.getAnnotator());
                    assertEquals(2557, game.getWhitePlayer().getElo());
                    assertEquals(2308, game.getBlackPlayer().getElo());

                    assertEquals("C67", game.getEco());
                    assertEquals(143, game.getHalfMoves().size());
                    assertEquals("e2e4 e7e5 g1f3 b8c6 f1b5 g8f6 e1g1 f6e4 d2d4 f8e7 d1e2 e4d6 b5c6 b7c6 " +
                            "d4e5 d6b7 f3d4 e8g8 b1c3 e7c5 c1e3 f8e8 f2f4 d7d6 a1d1 c8d7 e2f3 c5b6 e3f2 d8c8 h2h3 " +
                            "c6c5 d4b5 d6d5 d1d5 d7c6 b5a3 b6a5 c3d1 c6d5 f3d5 a5b6 a3c4 c8e6 d5e6 f7e6 f2h4 b7d8 " +
                            "d1e3 e8f8 g2g3 d8c6 g1g2 h7h6 g3g4 a8e8 a2a3 a7a5 a3a4 f8f7 c2c3 b6a7 h4g3 f7d7 f4f5 " +
                            "a7b6 g2f3 b6a7 f3e4 d7f7 f1d1 g8h7 g3h4 a7b8 f5f6 g7g5 h4f2 b8a7 h3h4 e8g8 h4g5 h6g5 " +
                            "e3f1 h7g6 f1d2 f7h7 d2f3 g8d8 d1g1 h7h3 f2e3 a7b6 e3c1 h3f3 e4f3 d8d5 f3e4 c6d8 c1d2 " +
                            "d8f7 g1g2 b6a7 c4a5 d5e5 e4f3 e5d5 a5c6 a7b6 c6e7 g6f6 e7d5 e6d5 b2b4 f7e5 f3e2 c5b4 " +
                            "c3b4 f6g6 a4a5 b6a7 a5a6 c7c6 g2f2 e5g4 f2f8 g4e5 f8g8 g6f5 d2g5 e5d7 g8a8 a7b6 g5d8 " +
                            "b6g1 a8c8 d5d4 c8c6 d4d3 e2d2 d7e5 c6c1 g1d4 b4b5", game.getHalfMoves().toString());
                    break;
                }
                case 3: {
                    assertEquals("JabbaChess", game.getWhitePlayer().getName());
                    assertEquals("CapivaraLK", game.getBlackPlayer().getName());
                    assertEquals("2011.01.29", game.getDate());
                    assertEquals(3, game.getRound().getNumber());
                    assertEquals("1-0", game.getResult().getDescription());
                    assertEquals("64", game.getPlyCount());
                    assertEquals("Albert Silver", game.getAnnotator());
                    assertEquals(1680, game.getWhitePlayer().getElo());
                    assertEquals(0, game.getBlackPlayer().getElo());

                    assertEquals("E62", game.getEco());
                    assertEquals(64, game.getHalfMoves().size());
                    assertEquals("d2d4 g8f6 c2c4 g7g6 g1f3 f8g7 g2g3 e8g8 b1c3 d7d6 f1g2 b8c6 e1g1 c8g4 " +
                            "d4d5 c6a5 b2b3 f6d5 c3d5 g7a1 c1d2 g4f3 g2f3 a5c4 b3c4 a1g7 d1b3 c7c6 d5b4 e7e5 f1d1 " +
                            "f7f5 d2c3 f8f7 c4c5 d6d5 b4d5 c6d5 f3d5 d8c7 d5e6 g8f8 c3b4 f7e7 c5c6 c7c6 e6g8 c6f6 " +
                            "g8h7 f8e8 b3g8 g7f8 h7g6 f6g6 g8g6 e7f7 g6e6 f7e7 b4e7 f8e7 d1d7 e8f8 e6e7 f8g8",
                            game.getHalfMoves().toString());
                    break;
                }
                default: {
                    break;
                }

            }
        }

    }
}
