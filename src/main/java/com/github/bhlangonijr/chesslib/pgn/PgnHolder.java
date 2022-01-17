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

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.bhlangonijr.chesslib.game.Event;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.game.Player;
import com.github.bhlangonijr.chesslib.game.Round;
import com.github.bhlangonijr.chesslib.util.LargeFile;

/**
 * A proxy for accessing a Portable Game Notation (PGN) file. The PGN holder can be used to optimize the way the
 * contents of the file are retrieved, and also to abstract the common operations that can be performed with the file,
 * such as saving to the PGN file the games held into memory, as well as retrieving the metadata stored in the PGN.
 */
public class PgnHolder {

    private final Map<String, Event> event = new HashMap<String, Event>();
    private final Map<String, Player> player = new HashMap<String, Player>();
    private final List<Game> games = new ArrayList<Game>();
    private final List<PgnLoadListener> listener = new ArrayList<PgnLoadListener>();
    private String fileName;
    private Integer size;
    private boolean lazyLoad;

    /**
     * Constructs a new PGN holder using the provided filename as a reference to the PGN file.
     *
     * @param filename the PGN filename
     */
    public PgnHolder(String filename) {
        setFileName(filename);
        setLazyLoad(false);
    }

    /**
     * Resets the status of the holder, cleaning up all data previously stored.
     */
    public void cleanUp() {
        event.clear();
        player.clear();
        games.clear();
        listener.clear();
        size = 0;
    }

    /**
     * Returns the filename of the PGN file.
     *
     * @return the filename of the PGN
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets a new filename for the PGN file.
     *
     * @param fileName the filename of the PGN
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Returns all the chess events stored in the holder, accessible by name.
     *
     * @return the chess events
     */
    public Map<String, Event> getEvent() {
        return event;
    }

    /**
     * Returns all the chess players stored in the holder, accessible by ID.
     *
     * @return the chess players
     */
    public Map<String, Player> getPlayer() {
        return player;
    }

    /**
     * Returns all the games stored in the holder.
     *
     * @return the games
     */
    public List<Game> getGames() {
        return games;
    }

    /**
     * Returns all the games stored in the holder.
     *
     * @return the games
     * @deprecated use {@link PgnHolder#getGames()} instead
     */
    @Deprecated
    public List<Game> getGame() {
        return games;
    }

    /**
     * Loads into memory the chess data stored in the PGN file referred by the holder.
     *
     * @throws Exception in case of error loading the contents of the file
     */
    public void loadPgn() throws Exception {
        loadPgn(new LargeFile(getFileName()));
    }

    /**
     * Counts the games present in the PGN file.
     * <p>
     * It does not load the contents of the file, but rather checks into the data how many events are persisted. In
     * order to do so, the implementation counts the mandatory PGN tags.
     *
     * @return the number of games in PGN file
     * @throws IOException in case of error reading the PGN file
     */
    public long countGamesInPgnFile() throws IOException {
        return Files.lines(Paths.get(this.fileName))
                .filter(s -> s.startsWith("[Event "))
                .count();
    }

    /**
     * Loads into memory the chess data stored in the given PGN file.
     *
     * @param file the PGN file to load
     * @throws Exception in case of error loading the contents of the file
     */
    public void loadPgn(LargeFile file) throws Exception {

        size = 0;

        PgnIterator games = new PgnIterator(file);

        try {
            for (Game game : games) {
                addGame(game);
            }
        } finally {
            file.close();
        }
    }

    /**
     * Loads into memory the chess data of the given PGN, provided as a raw string representation.
     *
     * @param pgn the raw string representing the contents of a PGN
     */
    public void loadPgn(String pgn) {

        Iterable<String> iterable = Arrays.asList(pgn.split("\n"));
        PgnIterator games = new PgnIterator(iterable.iterator());
        for (Game game : games) {
            addGame(game);
        }
    }

    /**
     * Saves to the PGN file the current data stored in the holder.
     */
    public void savePgn() {

        try {
            FileWriter outFile = new FileWriter(getFileName());
            PrintWriter out = new PrintWriter(outFile);
            for (Event event : getEvent().values()) {
                for (Round round : event.getRound().values()) {
                    for (Game game : round.getGame()) {
                        if (game != null) {
                            out.println();
                            out.print(game);
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
     * Returns the number of games stored in holder.
     *
     * @return the number of games
     */
    public Integer getSize() {
        return size;
    }

    /**
     * Checks if the PGN contents are loaded lazily.
     * <p>
     * <b>N.B.</b>: at the moment lazy loading is not enabled and this flag has no impact on the behavior of the class.
     *
     * @return {@code true} if the PGN contents are loaded lazily
     */
    public boolean isLazyLoad() {
        return lazyLoad;
    }

    /**
     * Sets whether to activate lazy loading or not.
     * <p>
     * <b>N.B.</b>: at the moment lazy loading is not enabled and this flag has no impact on the behavior of the class.
     *
     * @param lazyLoad {@code true} to activate lazy loading
     */
    public void setLazyLoad(boolean lazyLoad) {
        this.lazyLoad = lazyLoad;
    }

    /**
     * Returns the list of observers to the PGN loading events. The list can be used to add other listeners or remove
     * existing ones.
     *
     * @return the listeners to PGN loading events
     */
    public List<PgnLoadListener> getListener() {
        return listener;
    }

    /**
     * Returns a string representation of this PGN holder.
     *
     * @return a string representation of the holder
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Event event : getEvent().values()) {
            for (Round round : event.getRound().values()) {
                for (Game game : round.getGame()) {
                    if (game != null) {
                        sb.append('\n');
                        sb.append(game);
                        sb.append('\n');
                    }
                }
            }
        }
        return sb.toString();
    }

    private void addGame(Game game) {

        Event event = getEvent().get(game.getRound().getEvent().getName());
        if (event == null) {
            getEvent().put(game.getRound().getEvent().getName(), game.getRound().getEvent());
        }
        Player whitePlayer = getPlayer().get(game.getWhitePlayer().getId());
        if (whitePlayer == null) {
            getPlayer().put(game.getWhitePlayer().getId(), game.getWhitePlayer());
        }
        Player blackPlayer = getPlayer().get(game.getBlackPlayer().getId());
        if (blackPlayer == null) {
            getPlayer().put(game.getBlackPlayer().getId(), game.getBlackPlayer());
        }
        this.games.add(game);

        // Notify all registered listeners about added game
        this.getListener().forEach(pgnLoadListener -> pgnLoadListener.notifyProgress(this.games.size()));
    }
}
