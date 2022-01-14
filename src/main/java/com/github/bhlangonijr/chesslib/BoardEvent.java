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

package com.github.bhlangonijr.chesslib;

/**
 * An event emitted whenever a change of status happens or a move is played on the board.
 * <p>
 * Board events can be listened using implementations of the {@link BoardEventListener} interface.
 */
public interface BoardEvent {

    /**
     * Returns the type of the board event.
     *
     * @return the type of the event
     */
    BoardEventType getType();

}
