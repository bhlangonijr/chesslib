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

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.util.LargeFile;

import java.util.Iterator;

/**
 * The type Pgn Iterator.
 * <p>
 * The pgn iterator permits iterating over large PGN files without piling up every game in the memory
 */
public class PgnIterator implements Iterable<Game>, AutoCloseable {

    private final Iterator<String> pgnLines;

    private Game game;

    /**
     * Instantiates a new Pgn holder.
     *
     * @param filename the filename
     * @throws Exception reading the file
     */
    public PgnIterator(String filename) throws Exception {

        this(new LargeFile(filename));
    }

    public PgnIterator(LargeFile file) {

        this.pgnLines = file.iterator();
        loadNextGame();
    }

    public PgnIterator(Iterable<String> pgnLines) {

        this.pgnLines = pgnLines.iterator();
        loadNextGame();
    }

    public PgnIterator(Iterator<String> pgnLines) {

        this.pgnLines = pgnLines;
        loadNextGame();
    }

    @Override
    public Iterator<Game> iterator() {
        return new GameIterator();
    }

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