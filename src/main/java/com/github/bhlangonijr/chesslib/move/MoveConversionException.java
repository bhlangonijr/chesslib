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

public class MoveConversionException extends Exception {

    private static final long serialVersionUID = 5523540383760826752L;

    public MoveConversionException() {
        super();
    }

    public MoveConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public MoveConversionException(String message) {
        super(message);
    }

    public MoveConversionException(Throwable cause) {
        super(cause);
    }

}
