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
 * Thrown to indicate a failure converting a move from a textual representation, most likely because of an invalid
 * or unknown syntax.
 */
public class MoveConversionException extends RuntimeException {

    private static final long serialVersionUID = 5523540383760826752L;

    /**
     * Constructs a new move conversion exception.
     */
    public MoveConversionException() {
        super();
    }

    /**
     * Constructs a new move conversion exception with the specified detail message and cause.
     *
     * @param message the error message
     * @param cause   the cause
     */
    public MoveConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new move conversion exception with the specified detail message.
     *
     * @param message the error message
     */
    public MoveConversionException(String message) {
        super(message);
    }

    /**
     * Constructs a new move conversion exception with the specified cause.
     *
     * @param cause the cause
     */
    public MoveConversionException(Throwable cause) {
        super(cause);
    }

}
