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

package com.github.bhlangonijr.chesslib.move;

/**
 * Thrown to indicate a failure generating a move.
 */
public class MoveGeneratorException extends RuntimeException {

    private static final long serialVersionUID = 6523240383760826752L;

    /**
     * Constructs a new move generator exception.
     */
    public MoveGeneratorException() {
        super();
    }

    /**
     * Constructs a new move generator exception with the specified detail message and cause.
     *
     * @param message the error message
     * @param cause   the cause
     */
    public MoveGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new move generator exception with the specified detail message.
     *
     * @param message the error message
     */
    public MoveGeneratorException(String message) {
        super(message);
    }

    /**
     * Constructs a new move generator exception with the specified cause.
     *
     * @param cause the cause
     */
    public MoveGeneratorException(Throwable cause) {
        super(cause);
    }

}
