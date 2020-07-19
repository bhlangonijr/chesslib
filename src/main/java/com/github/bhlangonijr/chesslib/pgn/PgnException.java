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

/**
 * The type Pgn exception.
 */
public class PgnException extends RuntimeException {

    private static final long serialVersionUID = 6523240383760826752L;

    /**
     * Instantiates a new Pgn exception.
     */
    public PgnException() {
        super();
    }

    /**
     * Instantiates a new Pgn exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public PgnException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Pgn exception.
     *
     * @param message the message
     */
    public PgnException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Pgn exception.
     *
     * @param cause the cause
     */
    public PgnException(Throwable cause) {
        super(cause);
    }

}
