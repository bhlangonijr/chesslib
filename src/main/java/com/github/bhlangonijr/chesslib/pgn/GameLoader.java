/*
 * Copyright 2017 Ben-Hur Carlos Vieira Langoni Junior
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.bhlangonijr.chesslib.pgn;

import com.github.bhlangonijr.chesslib.game.*;
import com.github.bhlangonijr.chesslib.util.StringUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import static com.github.bhlangonijr.chesslib.pgn.PgnProperty.*;

/**
 * The type Game Loader.
 * <p>
 * The game loader permits loading a single PGN game
 */
public class GameLoader {

    public static Game loadNextGame(Iterator<String> iterator) {

        Event event = null;
        Round round = null;
        Game game = null;
        Player whitePlayer = null;
        Player blackPlayer = null;
        StringBuilder moveText = null;
        boolean moveTextParsing = false;

        while (iterator.hasNext()) {
            String line = iterator.next();
            try {
                line = line.trim();
                if (line.startsWith(UTF8_BOM)) {
                    line = line.substring(1);
                }
                if (isProperty(line)) {
                    PgnProperty p = parsePgnProperty(line);
                    if (p != null) {
                        String tag = p.name.toLowerCase().trim();
                        //begin
                        switch (tag) {
                            case "event":
                                if (moveTextParsing && game != null && game.getHalfMoves().size() == 0) {
                                    setMoveText(game, moveText);
                                }
                                game = null;
                                round = null;
                                whitePlayer = null;
                                blackPlayer = null;
                                if (event == null) {
                                    event = GameFactory.newEvent(p.value);
                                }
                                moveText = new StringBuilder();

                                break;
                            case "site":
                                if (event != null) {
                                    event.setSite(p.value);
                                }
                                break;
                            case "date":
                                if (event != null) {
                                    event.setStartDate(p.value);
                                }
                                break;
                            case "round":
                                if (event != null) {
                                    int r = 1;
                                    try {
                                        r = Integer.parseInt(p.value);
                                    } catch (Exception e1) {
                                    }
                                    r = Math.max(0, r);
                                    round = event.getRound().get(r);
                                    if (round == null) {
                                        round = GameFactory.newRound(event, r);
                                        event.getRound().put(r, round);
                                    }
                                }
                                break;
                            case "white": {
                                if (round == null) {
                                    round = GameFactory.newRound(event, 1);
                                    event.getRound().put(1, round);
                                }
                                if (game == null) {
                                    game = GameFactory.newGame(UUID.randomUUID().toString(), round);
                                    game.setDate(event.getStartDate());
                                    round.getGame().add(game);
                                }

                                Player player = GameFactory.newPlayer(PlayerType.HUMAN, p.value);
                                player.setId(p.value);
                                player.setDescription(p.value);

                                game.setWhitePlayer(player);
                                whitePlayer = player;

                                break;
                            }
                            case "black": {
                                if (round == null) {
                                    round = GameFactory.newRound(event, 1);
                                    event.getRound().put(1, round);
                                }
                                if (game == null) {
                                    game = GameFactory.newGame(UUID.randomUUID().toString(), round);
                                    game.setDate(event.getStartDate());
                                    round.getGame().add(game);
                                }
                                Player player = GameFactory.newPlayer(PlayerType.HUMAN, p.value);
                                player.setId(p.value);
                                player.setDescription(p.value);

                                game.setBlackPlayer(player);
                                blackPlayer = player;

                                break;
                            }
                            case "result":
                                if (game != null) {
                                    GameResult r = GameResult.fromNotation(p.value);
                                    game.setResult(r);
                                }
                                break;
                            case "plycount":
                                if (game != null) {
                                    game.setPlyCount(p.value);
                                }
                                break;
                            case "termination":
                                if (game != null) {
                                    try {
                                        game.setTermination(Termination.fromValue(p.value.toUpperCase()));
                                    } catch (Exception e1) {
                                        game.setTermination(Termination.UNTERMINATED);
                                    }
                                }
                                break;
                            case "timecontrol":
                                if (event != null && event.getTimeControl() == null) {
                                    try {
                                        event.setTimeControl(TimeControl.parseFromString(p.value.toUpperCase()));
                                    } catch (Exception e1) {
                                        //ignore errors in time control tag as it's not required by standards
                                    }
                                }
                                break;
                            case "annotator":
                                if (game != null) {
                                    game.setAnnotator(p.value);
                                }
                                break;
                            case "fen":
                                if (game != null) {
                                    game.setFen(p.value);
                                }
                                break;
                            case "eco":
                                if (game != null) {
                                    game.setEco(p.value);
                                }
                                break;
                            case "opening":
                                if (game != null) {
                                    game.setOpening(p.value);
                                }
                                break;
                            case "variation":
                                if (game != null) {
                                    game.setVariation(p.value);
                                }
                                break;
                            case "whiteelo":
                                if (whitePlayer != null) {
                                    try {
                                        whitePlayer.setElo(Integer.parseInt(p.value));
                                    } catch (NumberFormatException e) {

                                    }
                                }
                                break;
                            case "blackelo":
                                if (blackPlayer != null) {
                                    try {
                                        blackPlayer.setElo(Integer.parseInt(p.value));
                                    } catch (NumberFormatException e) {

                                    }
                                }
                                break;
                            default:
                                if (game != null) {
                                    if (game.getProperty() == null) {
                                        game.setProperty(new HashMap<String, String>());
                                    }
                                    game.getProperty().put(p.name, p.value);
                                }
                                break;
                        }
                    }
                } else if (!line.trim().equals("") && moveText != null) {
                    moveText.append(line);
                    moveText.append('\n');
                    moveTextParsing = true;
                    if (line.endsWith("1-0") ||
                            line.endsWith("0-1") ||
                            line.endsWith("1/2-1/2") ||
                            line.endsWith("*")) {
                        //end of PGN
                        if (game != null) {
                            setMoveText(game, moveText);
                        }
                        break;
                    }
                }

            } catch (Exception e) {
                String name = "";
                int r = 0;
                try {
                    r = round.getNumber();
                    name = event.getName();
                } catch (Exception e2) {

                }
                throw new PgnException("Error parsing PGN[" + r + ", " + name + "]: ", e);
            }

        }

        return game;
    }

    private static void setMoveText(Game game, StringBuilder moveText) throws Exception {

        //clear game result
        StringUtil.replaceAll(moveText, "1-0", "");
        StringUtil.replaceAll(moveText, "0-1", "");
        StringUtil.replaceAll(moveText, "1/2-1/2", "");
        StringUtil.replaceAll(moveText, "*", "");

        game.setMoveText(moveText);
        game.loadMoveText(moveText);

        game.setPlyCount(game.getHalfMoves().size() + "");

    }
}