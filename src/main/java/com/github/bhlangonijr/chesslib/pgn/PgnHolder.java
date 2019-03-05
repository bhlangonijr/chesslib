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
import com.github.bhlangonijr.chesslib.util.LargeFile;
import com.github.bhlangonijr.chesslib.util.StringUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The type Pgn holder.
 */
public class PgnHolder {

    private final static Pattern propertyPattern = Pattern.compile("\\[.* \".*\"\\]");
    private final static String UTF8_BOM = "\uFEFF";
    private final Map<String, Event> event = new HashMap<String, Event>();
    private final Map<String, Player> player = new HashMap<String, Player>();
    private final List<Game> game = new ArrayList<Game>();
    private final List<PgnLoadListener> listener = new ArrayList<PgnLoadListener>();
    private String fileName;
    private Integer size;
    private boolean lazyLoad;

    /**
     * Instantiates a new Pgn holder.
     *
     * @param filename the filename
     */
    public PgnHolder(String filename) {
        setFileName(filename);
        setLazyLoad(false);
    }

    private static boolean isProperty(String line) {
        return propertyPattern.matcher(line).matches();
    }

    private static PgnProperty parsePgnProperty(String line) {
        try {

            String l = line.replace("[", "");
            l = l.replace("]", "");
            l = l.replace("\"", "");

            return new PgnProperty(StringUtil.beforeSequence(l, " "),
                    StringUtil.afterSequence(l, " "));
        } catch (Exception e) {
            // do nothing
        }

        return null;
    }

    /**
     * Clean up.
     */
    public void cleanUp() {
        event.clear();
        player.clear();
        game.clear();
        listener.clear();
        size = 0;
    }

    /**
     * Gets file name.
     *
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets file name.
     *
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets event.
     *
     * @return the event
     */
    public Map<String, Event> getEvent() {
        return event;
    }

    /**
     * Gets player.
     *
     * @return the player
     */
    public Map<String, Player> getPlayer() {
        return player;
    }

    /**
     * Gets game.
     *
     * @return the game
     */
    public List<Game> getGame() {
        return game;
    }

    /**
     * Load the PGN file
     *
     * @throws Exception the exception
     */
    public void loadPgn() throws Exception {
        LargeFile file = new LargeFile(getFileName());
        size = 0;
        Event event = null;
        Round round = null;
        Game game = null;
        Player whitePlayer = null;
        Player blackPlayer = null;
        StringBuilder moveText = null;
        boolean moveTextParsing = false;
        try {
            for (String line : file) {
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
                            if (tag.equals("event")) {
                                if (moveTextParsing && moveText != null && game != null &&
                                        game.getHalfMoves().size() == 0) {
                                    setMoveText(game, moveText);
                                }
                                size++;
                                for (PgnLoadListener l : getListener()) {
                                    l.notifyProgress(size);
                                }
                                game = null;
                                round = null;
                                whitePlayer = null;
                                blackPlayer = null;
                                event = getEvent().get(p.value);
                                if (event == null) {
                                    event = GameFactory.newEvent(p.value);
                                    event.setPgnHolder(this);
                                    getEvent().put(p.value, event);
                                }
                                moveText = new StringBuilder();

                            } else if (tag.equals("site")) {
                                if (event != null) {
                                    event.setSite(p.value);
                                }
                            } else if (tag.equals("date")) {
                                if (event != null) {
                                    event.setStartDate(p.value);
                                }
                            } else if (tag.equals("round")) {
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
                            } else if (tag.equals("white")) {
                                if (round == null) {
                                    round = GameFactory.newRound(event, 1);
                                    event.getRound().put(1, round);
                                }
                                if (game == null) {
                                    game = GameFactory.newGame((size - 1) + "", round);
                                    game.setDate(event.getStartDate());
                                    round.getGame().add(game);
                                    getGame().add(size - 1, game);
                                }

                                Player player = getPlayer().get(p.value);
                                if (player == null) {
                                    player = GameFactory.newPlayer(PlayerType.HUMAN, p.value);
                                    player.setId(p.value);
                                    player.setDescription(p.value);
                                    getPlayer().put(p.value, player);
                                }
                                game.setWhitePlayer(player);
                                whitePlayer = player;

                            } else if (tag.equals("black")) {
                                if (round == null) {
                                    round = GameFactory.newRound(event, 1);
                                    event.getRound().put(1, round);
                                }
                                if (game == null) {
                                    game = GameFactory.newGame((size - 1) + "", round);
                                    game.setDate(event.getStartDate());
                                    round.getGame().add(game);
                                    getGame().add(size - 1, game);
                                }
                                Player player = getPlayer().get(p.value);
                                if (player == null) {
                                    player = GameFactory.newPlayer(PlayerType.HUMAN, p.value);
                                    player.setId(p.value);
                                    player.setDescription(p.value);
                                    getPlayer().put(p.value, player);
                                }
                                game.setBlackPlayer(player);
                                blackPlayer = player;

                            } else if (tag.equals("result")) {
                                if (game != null) {
                                    GameResult r = GameResult.fromNotation(p.value);
                                    game.setResult(r);
                                }
                            } else if (tag.equals("plycount")) {
                                if (game != null) {
                                    game.setPlyCount(p.value);
                                }
                            } else if (tag.equals("termination")) {
                                if (game != null) {
                                    try {
                                        game.setTermination(Termination.fromValue(p.value.toUpperCase()));
                                    } catch (Exception e1) {
                                        game.setTermination(Termination.UNTERMINATED);
                                    }
                                }
                            } else if (tag.equals("timecontrol")) {
                                if (event != null && event.getTimeControl() == null) {
                                    try {
                                        event.setTimeControl(TimeControl.parseFromString(p.value.toUpperCase()));
                                    } catch (Exception e1) {
                                        throw new PgnException("Error parsing TimeControl Tag [" + (round != null ? round.getNumber() : 1) +
                                                ", " + event.getName() + "]: " + e1.getMessage());
                                    }
                                }
                            } else if (tag.equals("annotator")) {
                                if (game != null) {
                                    game.setAnnotator(p.value);
                                }
                            } else if (tag.equals("fen")) {
                                if (game != null) {
                                    game.setFen(p.value);
                                }
                            } else if (tag.equals("eco")) {
                                if (game != null) {
                                    game.setEco(p.value);
                                }
                            } else if (tag.equals("opening")) {
                                if (game != null) {
                                    game.setOpening(p.value);
                                }
                            } else if (tag.equals("variation")) {
                                if (game != null) {
                                    game.setVariation(p.value);
                                }
                            } else if (tag.equals("whiteelo")) {
                                if (whitePlayer != null) {
                                    try {
                                        whitePlayer.setElo(Integer.parseInt(p.value));
                                    } catch (NumberFormatException e) {

                                    }
                                }
                            } else if (tag.equals("blackelo")) {
                                if (blackPlayer != null) {
                                    try {
                                        blackPlayer.setElo(Integer.parseInt(p.value));
                                    } catch (NumberFormatException e) {

                                    }
                                }
                            } else {
                                if (game != null) {
                                    if (game.getProperty() == null) {
                                        game.setProperty(new HashMap<String, String>());
                                    }
                                    game.getProperty().put(p.name, p.value);
                                }
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
                            moveText = null;
                            moveTextParsing = false;
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
        } finally {
            file.close();
        }
    }

    private void setMoveText(Game game, StringBuilder moveText) throws Exception {

        //clear game result
        StringUtil.replaceAll(moveText, "1-0", "");
        StringUtil.replaceAll(moveText, "0-1", "");
        StringUtil.replaceAll(moveText, "1/2-1/2", "");
        StringUtil.replaceAll(moveText, "*", "");

        if (isLazyLoad()) {
            game.loadMoveText(moveText);
        } else {
            game.setMoveText(moveText);
        }
        game.setPlyCount(game.getHalfMoves().size() + "");

    }

    /**
     * Save the PGN
     */
    public void savePGN() {

        try {
            FileWriter outFile = new FileWriter(getFileName());
            PrintWriter out = new PrintWriter(outFile);
            for (Event event : getEvent().values()) {
                for (Round round : event.getRound().values()) {
                    for (Game game : round.getGame()) {
                        if (game != null) {
                            out.println();
                            out.print(game.toString());
                            out.println();
                        }
                    }
                }
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Gets size.
     *
     * @return the size
     */
    public Integer getSize() {
        return size;
    }

    /**
     * Is lazy load boolean.
     *
     * @return the lazyLoad
     */
    public boolean isLazyLoad() {
        return lazyLoad;
    }

    /**
     * Sets lazy load.
     *
     * @param lazyLoad the lazyLoad to set
     */
    public void setLazyLoad(boolean lazyLoad) {
        this.lazyLoad = lazyLoad;
    }

    /**
     * Gets listener.
     *
     * @return the listener
     */
    public List<PgnLoadListener> getListener() {
        return listener;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Event event : getEvent().values()) {
            for (Round round : event.getRound().values()) {
                for (Game game : round.getGame()) {
                    if (game != null) {
                        sb.append('\n');
                        sb.append(game.toString());
                        sb.append('\n');
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * The type Pgn property.
     */
    static class PgnProperty {
        /**
         * The Name.
         */
        public String name;

        /**
         * The Value.
         */
        public String value;

        /**
         * Instantiates a new Pgn property.
         */
        public PgnProperty() {
        }

        /**
         * Instantiates a new Pgn property.
         *
         * @param name  the name
         * @param value the value
         */
        public PgnProperty(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }


}
