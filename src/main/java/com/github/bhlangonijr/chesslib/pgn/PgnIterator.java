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

import java.util.Iterator;

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.util.LargeFile;

/**
 * A Portable Game Notation (PGN) iterator, used to navigate the games contained in PGN file.
 * <p/>
 * The iterator permits iterating over large PGN files without piling up every game into the memory.
 */
public class PgnIterator implements Iterable<Game>, AutoCloseable {

    private final Iterator<String> pgnLines;

    private Game game;

    /**
     * Constructs a new PGN iterator from the filename of the PGN file.
     *
     * @param filename the PGN filename
     * @throws Exception in case the PGN file can not be accessed
     */
    public PgnIterator(String filename) throws Exception {

        this(new LargeFile(filename));
    }

    /**
     * Constructs a new PGN iterator from the PGN file.
     *
     * @param file the PGN file
     */
    public PgnIterator(LargeFile file) {

        this.pgnLines = file.iterator();
        loadNextGame();
    }

    /**
     * Constructs a new PGN iterator from an {@link Iterable} object that can iterate over the lines of the PGN file.
     *
     * @param pgnLines an iterable over the PGN lines
     */
    public PgnIterator(Iterable<String> pgnLines) {

        this.pgnLines = pgnLines.iterator();
        loadNextGame();
    }

    /**
     * Constructs a new PGN iterator from another iterator over the lines of the PGN file.
     *
     * @param pgnLines an iterator over PGN lines
     */
    public PgnIterator(Iterator<String> pgnLines) {

        this.pgnLines = pgnLines;
        loadNextGame();
    }

    /**
     * Returns an iterator over the games included in the PGN file.
     *
     * @return the iterator to navigate the games stored in the PGN file
     */
    @Override
    public Iterator<Game> iterator() {
        return new GameIterator();
    }

    /**
     * Attempts to close the PGN file and releases any system resources associated with it.
     */
    @Override
    public void close() throws Exception {

        if (pgnLines instanceof LargeFile) {
            ((LargeFile) (pgnLines)).close();
        }
    }

    private void loadNextGame() {

        game = GameLoader.loadNextGame(pgnLines);
    }

    private class GameIterator implements Iterator<Game> {

        public boolean hasNext() {

            return game != null;
        }

        public Game next() {

            Game current = game;
            loadNextGame();
            return current;
        }

        public void remove() {
        }
    }
}