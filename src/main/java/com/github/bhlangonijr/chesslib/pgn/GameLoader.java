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

import com.github.bhlangonijr.chesslib.game.Event;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.game.GameFactory;
import com.github.bhlangonijr.chesslib.game.GameResult;
import com.github.bhlangonijr.chesslib.game.Player;
import com.github.bhlangonijr.chesslib.game.PlayerType;
import com.github.bhlangonijr.chesslib.game.Round;
import com.github.bhlangonijr.chesslib.game.Termination;
import com.github.bhlangonijr.chesslib.game.TimeControl;
import com.github.bhlangonijr.chesslib.util.StringUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import static com.github.bhlangonijr.chesslib.pgn.PgnProperty.UTF8_BOM;
import static com.github.bhlangonijr.chesslib.pgn.PgnProperty.isProperty;
import static com.github.bhlangonijr.chesslib.pgn.PgnProperty.parsePgnProperty;

/**
 * A convenient loader to extract a chess game and its metadata from an iterator over the lines of the PGN file.
 * <p>
 * The implementation allows loading only a single PGN game at a time.
 */
public class GameLoader {

    /**
     * Loads the next game of chess from an iterator over the lines of a Portable Game Notation (PGN) file. The
     * iteration ends when the game is fully loaded, hence the iterator is not consumed more than necessary.
     *
     * @param iterator the iterator over the lines of a PGN file
     * @return the next game read from the iterator
     */
    public static Game loadNextGame(Iterator<String> iterator) {

        PgnTempContainer container = new PgnTempContainer();

        while (iterator.hasNext()) {
            String line = iterator.next();
            try {
                line = line.trim();
                if (line.startsWith(UTF8_BOM)) {
                    line = line.substring(1);
                }
                if (isProperty(line)) {
                    addProperty(line, container);
                } else if (!line.equals("") && container.moveText != null) {
                    addMoveText(line, container);
                    if (isEndGame(line)) {
                        if (container.game != null) {
                            setMoveText(container.game, container.moveText);
                        }
                        return container.game;
                    }
                }
            } catch (Exception e) { //TODO stricter exceptions
                String name = container.event.getName();
                int r = container.round.getNumber();
                throw new PgnException("Error parsing PGN[" + r + ", " + name + "]: ", e);
            }
        }
        return container.game;
    }

    private static void addProperty(String line, PgnTempContainer container) throws Exception {
        PgnProperty property = parsePgnProperty(line);
        if (property == null) {
            return;
        }
        String tag = property.name.toLowerCase().trim();
        //begin
        switch (tag) {
            case "event":
                if (container.moveTextParsing && container.game != null && container.game.getHalfMoves().size() == 0) {
                    setMoveText(container.game, container.moveText);
                }
                container.event.setName(property.value);
                container.event.setId(property.value);
                container.moveText = new StringBuilder(); //TODO initialize this
                break;
            case "site":
                container.event.setSite(property.value);
                break;
            case "date":
                container.event.setStartDate(property.value);
                break;
            case "round":
                int r = 1;
                try {
                    r = Integer.parseInt(property.value); //TODO isParseable
                } catch (Exception e1) {
                }
                r = Math.max(0, r);
                container.round.setNumber(r);
                if (!container.event.getRound().containsKey(r)) {
                    container.event.getRound().put(r, container.round);
                }
                break;
            case "white": {
                if (container.round.getNumber() < 1) {
                    container.round.setNumber(1); //TODO this is just to have the same behaviour as before...
                }
                if (container.game == null) {
                    container.game = GameFactory.newGame(UUID.randomUUID().toString(), container.round);
                    container.game.setDate(container.event.getStartDate());
                    container.round.getGame().add(container.game);
                }

                Player player = GameFactory.newPlayer(PlayerType.HUMAN, property.value);
                player.setId(property.value);
                player.setDescription(property.value);

                container.game.setWhitePlayer(player);
                container.whitePlayer = player;

                break;
            }
            case "black": {
                if (container.round.getNumber() < 1) {
                    container.round.setNumber(1); //TODO this just to have the same behaviour as before...
                }
                if (container.game == null) {
                    container.game = GameFactory.newGame(UUID.randomUUID().toString(), container.round);
                    container.game.setDate(container.event.getStartDate());
                    container.round.getGame().add(container.game);
                }
                Player player = GameFactory.newPlayer(PlayerType.HUMAN, property.value);
                player.setId(property.value);
                player.setDescription(property.value);

                container.game.setBlackPlayer(player);
                container.blackPlayer = player;
                break;
            }
            case "result":
                if (container.game != null) {
                    GameResult result = GameResult.fromNotation(property.value);
                    container.game.setResult(result);
                }
                break;
            case "plycount":
                if (container.game != null) {
                    container.game.setPlyCount(property.value);
                }
                break;
            case "termination":
                if (container.game != null) {
                    try {
                        container.game.setTermination(Termination.fromValue(property.value.toUpperCase()));
                    } catch (Exception e1) {
                        container.game.setTermination(Termination.UNTERMINATED);
                    }
                }
                break;
            case "timecontrol":
                if (container.event.getTimeControl() == null) {
                    try {
                        container.event.setTimeControl(TimeControl.parseFromString(property.value.toUpperCase()));
                    } catch (Exception e1) {
                        //ignore errors in time control tag as it's not required by standards
                    }
                }
                break;
            case "annotator":
                if (container.game != null) {
                    container.game.setAnnotator(property.value);
                }
                break;
            case "fen":
                if (container.game != null) {
                    container.game.setFen(property.value);
                }
                break;
            case "eco":
                if (container.game != null) {
                    container.game.setEco(property.value);
                }
                break;
            case "opening":
                if (container.game != null) {
                    container.game.setOpening(property.value);
                }
                break;
            case "variation":
                if (container.game != null) {
                    container.game.setVariation(property.value);
                }
                break;
            case "whiteelo":
                if (container.whitePlayer != null) {
                    try {
                        container.whitePlayer.setElo(Integer.parseInt(property.value));
                    } catch (NumberFormatException e) {

                    }
                }
                break;
            case "blackelo":
                if (container.blackPlayer != null) {
                    try {
                        container.blackPlayer.setElo(Integer.parseInt(property.value));
                    } catch (NumberFormatException e) {

                    }
                }
                break;
            default:
                if (container.game != null) {
                    if (container.game.getProperty() == null) {
                        container.game.setProperty(new HashMap<String, String>());
                    }
                    container.game.getProperty().put(property.name, property.value);
                }
                break;
        }
    }

    private static void addMoveText(String line, PgnTempContainer container) {
        container.moveText.append(line);
        container.moveText.append('\n');
        container.moveTextParsing = true;
    }

    private static boolean isEndGame(String line) {
        return line.endsWith("1-0") || line.endsWith("0-1") || line.endsWith("1/2-1/2") || line.endsWith("*");
    }

    private static class PgnTempContainer {

        Event event = new Event();
        Round round = new Round(event);
        Game game;
        Player whitePlayer;
        Player blackPlayer;
        StringBuilder moveText;
        boolean moveTextParsing;

        PgnTempContainer() {}
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