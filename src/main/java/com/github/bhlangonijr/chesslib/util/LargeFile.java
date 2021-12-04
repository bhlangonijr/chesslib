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

package com.github.bhlangonijr.chesslib.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * The type Large file.
 */
public class LargeFile implements Iterable<String>, AutoCloseable {

    private final BufferedReader reader;

    private String nextLine;

    /**
     * Instantiates a new Large file.
     *
     * @param filePath the file path
     * @throws Exception the exception
     */
    public LargeFile(String filePath) throws Exception {

        reader = new BufferedReader(new FileReader(filePath));
        readNextLine();
    }

    /**
     * Instantiates a new Large file.
     *
     * @param inputStream the input stream
     */
    public LargeFile(InputStream inputStream) {

        reader = new BufferedReader(new InputStreamReader(inputStream));
        readNextLine();
    }

    @Override
    public void close() {
        try {
            reader.close();
        } catch (Exception ex) {
        }
    }

    public Iterator<String> iterator() {
        return new FileIterator();
    }

    private void readNextLine() {

        try {
            nextLine = reader.readLine();
        } catch (Exception ex) {
            nextLine = null;
            throw new IllegalStateException("Error reading file", ex);
        }
    }

    private class FileIterator implements Iterator<String> {

        public boolean hasNext() {

            return nextLine != null;
        }

        public String next() {

            String currentLine = nextLine;
            readNextLine();
            return currentLine;
        }

        public void remove() {
        }
    }
}
