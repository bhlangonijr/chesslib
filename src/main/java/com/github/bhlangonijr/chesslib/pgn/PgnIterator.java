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
public class PgnIterator implements Iterable<Game> {

    private Iterator<String> pgnLines;

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
    }

    public PgnIterator(Iterable<String> pgnLines) {

        this.pgnLines = pgnLines.iterator();
    }

    public PgnIterator(Iterator<String> pgnLines) {

        this.pgnLines = pgnLines;
    }


    @Override
    public Iterator<Game> iterator() {
        return new GameIterator();
    }

    private class GameIterator implements Iterator<Game> {
        private Game game;

        public boolean hasNext() {

            game = GameLoader.loadNextGame(pgnLines);
            return game != null;
        }

        public Game next() {
            return game;
        }

        public void remove() {
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (pgnLines instanceof LargeFile) {
            ((LargeFile) (pgnLines)).close();
        }
        super.finalize();
    }
}